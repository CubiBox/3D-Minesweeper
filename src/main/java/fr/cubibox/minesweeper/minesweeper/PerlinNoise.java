package fr.cubibox.minesweeper.minesweeper;

import java.util.Random;

public class PerlinNoise {
    static float[][] generatePerlinNoise(int size, long seed) {
        int octaveCount = 5;
        final float[][] base = new float[size][size];
        final float[][] perlinNoise = new float[size][size];
        final float[][][] noiseLayers = new float[octaveCount][][];

        Random random = new Random(seed);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                base[x][y] = random.nextFloat();
            }
        }

        for (int octave = 0; octave < octaveCount; octave++) {
            noiseLayers[octave] = generatePerlinNoiseLayer(base, size, size, octave);
        }

        float amplitude = 1f;
        float totalAmplitude = 0f;

        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= 0.1f;
            totalAmplitude += amplitude;

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    perlinNoise[x][y] += noiseLayers[octave][x][y] * amplitude;
                }
            }
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                perlinNoise[x][y] /= totalAmplitude;
            }
        }
        return perlinNoise;
    }

    static float[][] generatePerlinNoiseLayer(float[][] base, int width, int height, int octave) {
        float[][] perlinNoiseLayer = new float[width][height];

        //calculate period (wavelength) for different shapes
        int period = 1 << octave; //2^k
        float frequency = 1f / period; // 1/2^k

        for (int x = 0; x < width; x++) {
            //calculates the horizontal sampling indices
            int x0 = (x / period) * period;
            int x1 = (x0 + period) % width;
            float horizintalBlend = (x - x0) * frequency;

            for (int y = 0; y < height; y++) {
                //calculates the vertical sampling indices
                int y0 = (y / period) * period;
                int y1 = (y0 + period) % height;
                float verticalBlend = (y - y0) * frequency;

                //blend top corners
                float top = interpolate(base[x0][y0], base[x1][y0], horizintalBlend);

                //blend bottom corners
                float bottom = interpolate(base[x0][y1], base[x1][y1], horizintalBlend);

                //blend top and bottom interpolation to get the final blend value for this cell
                perlinNoiseLayer[x][y] = interpolate(top, bottom, verticalBlend);
            }
        }
        return perlinNoiseLayer;
    }

    static float interpolate(float a, float b, float alpha) {
        return a * (1 - alpha) + alpha * b;
    }

    public static byte[][] getHeightMap(int size, int seed, int height){
        final float[][] perlinNoise;

        perlinNoise = generatePerlinNoise(size, seed);
        final float step = 1f / height;

        byte[][] heightMap = new byte[size][size];
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++) {
                float value = step;
                for (int i = 0; i < height; i ++) {
                    if (perlinNoise[x][y] <= value) {
                        heightMap[y][x] = (byte)(value*height);
                        break;
                    }
                    value += step;
                }
            }
        return heightMap;
    }
}
