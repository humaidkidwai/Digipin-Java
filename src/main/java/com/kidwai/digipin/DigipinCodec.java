/*
 * MIT License
 *
 * Copyright (c) 2025 Humaid Kidwai
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.kidwai.digipin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class DigipinCodec {

    private static final int DIGIPIN_LENGTH = 10;
    private static final int GRID_SIZE = 4;

    public boolean validate(String digipin) {
        if (digipin == null) return false;
        String normalized = normalize(digipin);
        if (normalized.length() != DIGIPIN_LENGTH) return false;

        for (char c : normalized.toCharArray()) {
            if (!isValidGridChar(c)) return false;
        }
        return true;
    }

    public String encode(double lat, double lon) {
        validateBounds(lat, lon);

        StringBuilder encoded = new StringBuilder();
        double minLat = DigipinConstants.Bounds.MIN_LAT;
        double maxLat = DigipinConstants.Bounds.MAX_LAT;
        double minLon = DigipinConstants.Bounds.MIN_LON;
        double maxLon = DigipinConstants.Bounds.MAX_LON;

        for (int level = 1; level <= DIGIPIN_LENGTH; level++) {
            double latDiv = (maxLat - minLat) / GRID_SIZE;
            double lonDiv = (maxLon - minLon) / GRID_SIZE;

            int row = GRID_SIZE - 1 - (int) ((lat - minLat) / latDiv);
            int col = (int) ((lon - minLon) / lonDiv);

            row = clamp(row, 0, GRID_SIZE - 1);
            col = clamp(col, 0, GRID_SIZE - 1);

            encoded.append(DigipinConstants.GRID[row][col]);
            if (level == 3 || level == 6) encoded.append('-');

            // Narrow down bounds
            maxLat = minLat + latDiv * (GRID_SIZE - row);
            minLat += latDiv * (GRID_SIZE - 1 - row);
            minLon += lonDiv * col;
            maxLon = minLon + lonDiv;
        }

        return encoded.toString();
    }

    public double[] decode(String digipin) {
        String normalized = normalize(digipin);
        if (normalized.length() != DIGIPIN_LENGTH || !validate(digipin)) {
            throw new IllegalArgumentException("Invalid digipin: " + digipin);
        }

        double minLat = DigipinConstants.Bounds.MIN_LAT;
        double maxLat = DigipinConstants.Bounds.MAX_LAT;
        double minLon = DigipinConstants.Bounds.MIN_LON;
        double maxLon = DigipinConstants.Bounds.MAX_LON;

        for (char ch : normalized.toCharArray()) {
            Optional<GridIndex> grid = getGridIndex(ch);
            if (grid.isEmpty()) {
                throw new IllegalArgumentException("Invalid character in digipin: " + ch);
            }
            int row = grid.get().row();
            int col = grid.get().col();

            double latDiv = (maxLat - minLat) / GRID_SIZE;
            double lonDiv = (maxLon - minLon) / GRID_SIZE;

            maxLat = minLat + latDiv * (GRID_SIZE - row);
            minLat += latDiv * (GRID_SIZE - 1 - row);
            minLon += lonDiv * col;
            maxLon = minLon + lonDiv;
        }

        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;
        return new double[]{
                roundToSixDecimals(centerLat),
                roundToSixDecimals(centerLon)
        };
    }

    private void validateBounds(double lat, double lon) {
        if (lat < DigipinConstants.Bounds.MIN_LAT || lat > DigipinConstants.Bounds.MAX_LAT)
            throw new IllegalArgumentException("Latitude out of bounds: " + lat);
        if (lon < DigipinConstants.Bounds.MIN_LON || lon > DigipinConstants.Bounds.MAX_LON)
            throw new IllegalArgumentException("Longitude out of bounds: " + lon);
    }

    private boolean isValidGridChar(char ch) {
        return getGridIndex(ch).isPresent();
    }

    private Optional<GridIndex> getGridIndex(char ch) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (DigipinConstants.GRID[r][c] == ch) {
                    return Optional.of(new GridIndex(r, c));
                }
            }
        }
        return Optional.empty();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private double roundToSixDecimals(double value) {
        return new BigDecimal(value).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    private String normalize(String digipin) {
        return digipin == null ? "" : digipin.replace("-", "").trim().toUpperCase();
    }

    private record GridIndex(int row, int col) {}
}
