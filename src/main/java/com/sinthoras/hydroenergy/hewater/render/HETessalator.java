package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.proxy.HECommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class HETessalator {

    private static IntBuffer positionBuffer = GLAllocation.createDirectIntBuffer(3 * (1 << 12));
    private static IntBuffer waterIdBuffer = GLAllocation.createDirectIntBuffer(1 << 12);
    private static FloatBuffer worldColorModifierBuffer = GLAllocation.createDirectFloatBuffer(3 * (1 << 12));
    private static BitSet lightUpdateFlags = new BitSet(16*16*16);
    private static int numWaterBlocks = 0;

    private static Field frustrumX;
    private static Field frustrumY;
    private static Field frustrumZ;
    static {
        try {
            frustrumX = Frustrum.class.getDeclaredField("xPosition");
            frustrumX.setAccessible(true);
            frustrumY = Frustrum.class.getDeclaredField("yPosition");
            frustrumY.setAccessible(true);
            frustrumZ = Frustrum.class.getDeclaredField("zPosition");
            frustrumZ.setAccessible(true);
        } catch(Exception e) {}
    }

    private HashMap<Long, HESubChunk[]> chunks = new HashMap<Long, HESubChunk[]>();

    public HETessalator() {

    }

    public static final HETessalator instance = new HETessalator();

    public synchronized void onChunkUnload(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.remove(key);
    }

    public synchronized void onChunkLoad(int chunkX, int chunkZ) {
        long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
        chunks.put(key, new HESubChunk[] {
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk(),
                new HESubChunk(), new HESubChunk(), new HESubChunk(), new HESubChunk()
        });
    }

    public synchronized void onPreRender(World world, int x, int y, int z) {
        if(numWaterBlocks != 0) {
            lightUpdateFlags.clear();
            positionBuffer.clear();
            waterIdBuffer.clear();
            worldColorModifierBuffer.clear();
            numWaterBlocks = 0;
        }

        // TODO: Light update (gotta link update flag and waterID to provide patch with correct waterLevel
        // Or post render? hmmmm
        /*
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        for (int linearCoord = lightUpdateFlags.nextSetBit(0); linearCoord != -1; linearCoord = lightUpdateFlags.nextSetBit(linearCoord + 1))
            patchBlockLight(chunkX, chunkY, chunkZ, linearCoord >> 8, (linearCoord >> 4) & 15, linearCoord & 15, waterLevel, world, chunk);
        */
    }

    public synchronized void onPostRender(World world, int x, int y, int z) {
        if(numWaterBlocks != 0) {
            int chunkX = HEUtil.bucketInt16(x);
            int chunkY = HEUtil.bucketInt16(y);
            int chunkZ = HEUtil.bucketInt16(z);
            long key = HEUtil.chunkXZ2Int(chunkX, chunkZ);
            HESubChunk subChunk = chunks.get(key)[chunkY];

            if (subChunk.vaoId == -1) {
                subChunk.vaoId = GL30.glGenVertexArrays();
                subChunk.vboPositionId = GL15.glGenBuffers();
                subChunk.vboWaterIdId = GL15.glGenBuffers();
                subChunk.vboWorldColorModifierId = GL15.glGenBuffers();
            }

            positionBuffer.flip();
            waterIdBuffer.flip();
            worldColorModifierBuffer.flip();

            GL30.glBindVertexArray(subChunk.vaoId);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboPositionId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_INT, false, 0, 0);
            GL20.glEnableVertexAttribArray(0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboWaterIdId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, waterIdBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, 1, GL11.GL_INT, false, 0, 0);
            GL20.glEnableVertexAttribArray(1);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, subChunk.vboWorldColorModifierId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, worldColorModifierBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
            GL20.glEnableVertexAttribArray(2);

            GL30.glBindVertexArray(0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            subChunk.numWaterBlocks = numWaterBlocks;
        }
    }

    public synchronized void addBlock(int x, int y, int z, int waterId, Vector3f worldColorModifier, boolean[] shouldSideBeRendered) {
        waterId <<= 6;
        for(int i=0;i<shouldSideBeRendered.length;i++)
            if(shouldSideBeRendered[i])
                waterId |= 1 << i;

        // add to VBO
        positionBuffer.put(x);
        positionBuffer.put(y);
        positionBuffer.put(z);
        waterIdBuffer.put(waterId);
        worldColorModifierBuffer.put(worldColorModifier.x);
        worldColorModifierBuffer.put(worldColorModifier.y);
        worldColorModifierBuffer.put(worldColorModifier.z);
        numWaterBlocks++;

        // Light update stuff
        x = x & 15;
        y = y & 15;
        z = z & 15;
        lightUpdateFlags.set((x << 8) | (y << 4) | z);
    }

    public synchronized void render(ICamera frustrum, float partialTickTime) {
        if(MinecraftForgeClient.getRenderPass() == HECommonProxy.blockWaterStill.getRenderBlockPass()) {
            try {
                float x = (float)frustrumX.getDouble(frustrum);
                float y = (float)frustrumY.getDouble(frustrum);
                float z = (float)frustrumZ.getDouble(frustrum);
                HEProgram.calculateViewProjection(x, y, z);
            } catch(Exception e) {}

            // TODO: sort chunks?
            for (long key : chunks.keySet()) {
                int chunkX = (int) (key >> 32);
                int chunkZ = (int) key;
                for (int chunkY = 0; chunkY < 16; chunkY++) {
                    int x = HEUtil.debucketInt16(chunkX);
                    int y = HEUtil.debucketInt16(chunkY);
                    int z = HEUtil.debucketInt16(chunkZ);
                    // TODO: compare with WorldRenderer:112
                    if (frustrum.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(x, y, z, x + 16, y + 16, z + 16))) {
                        chunks.get(key)[chunkY].render(partialTickTime);
                    }
                }
            }
        }
    }
}
