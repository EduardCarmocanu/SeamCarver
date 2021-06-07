import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private double[][] energyMatrix;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.energyMatrix = this.buildEnergy();
    }

    private double[][] buildEnergy() {
        double[][] matrix = new double[picture.height()][picture.width()];
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                matrix[y][x] = this.energy(x, y);
            }
        }

        return matrix;
    }

    public Picture picture() {
        return this.picture;
    }

    public int width() {
        return this.picture.width();
    }

    public int height() {
        return this.picture.height();
    }

    public double energy(int x, int y) {
        if (y >= this.height() || y < 0 || x >= this.width() || x < 0) {
            throw new IllegalArgumentException();
        }

        if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1) {
            return 1000.0D;
        }

        return Math.sqrt((this.deltaXSquared(y, x) + this.deltaYSquared(y, x)));
    }

    private double deltaXSquared(int y, int x) {
        Color x1 = this.picture.get(x - 1, y);
        Color x2 = this.picture.get(x + 1, y);
        return Math.pow(x1.getRed() - x2.getRed(), 2) + Math
                .pow(x1.getGreen() - x2.getGreen(), 2) + Math
                .pow(x1.getBlue() - x2.getBlue(), 2);
    }

    private double deltaYSquared(int y, int x) {
        Color y1 = this.picture.get(x, y - 1);
        Color y2 = this.picture.get(x, y + 1);
        return Math.pow(y1.getRed() - y2.getRed(), 2) + Math
                .pow(y1.getGreen() - y2.getGreen(), 2) + Math
                .pow(y1.getBlue() - y2.getBlue(), 2);
    }

    private void transpose() {
        Picture transposedPic = new Picture(this.picture.height(), this.picture.width());
        double[][] transposedEnergy = new double[transposedPic.height()][transposedPic.width()];

        for (int y = 0; y < this.picture.height(); y++) {
            for (int x = 0; x < this.picture.width(); x++) {
                transposedPic.set(y, x, this.picture.get(x, y));
                transposedEnergy[x][y] = this.energyMatrix[y][x];
            }
        }

        this.energyMatrix = transposedEnergy;
        this.picture = transposedPic;
    }

    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    private void relax(int y, int x, double[][] distTo, int[][] from) {
        if (x - 1 >= 0 && y + 1 < this.height()) {
            double newDistance = distTo[y][x] + energyMatrix[y + 1][x - 1];

            if (distTo[y + 1][x - 1] > newDistance || distTo[y + 1][x - 1] == 0.0d) {
                distTo[y + 1][x - 1] = newDistance;
                from[y + 1][x - 1] = x;
            }
        }

        if (x >= 0 && y + 1 < this.height()) {
            double newDistance = distTo[y][x] + energyMatrix[y + 1][x];

            if (distTo[y + 1][x] > newDistance || distTo[y + 1][x] == 0.0d) {
                distTo[y + 1][x] = newDistance;
                from[y + 1][x] = x;
            }
        }

        if (x + 1 < this.width() && y + 1 < this.height()) {
            double newDistance = distTo[y][x] + energyMatrix[y + 1][x + 1];

            if (distTo[y + 1][x + 1] > newDistance || distTo[y + 1][x + 1] == 0.0d) {
                distTo[y + 1][x + 1] = newDistance;
                from[y + 1][x + 1] = x;
            }
        }
    }

    public int[] findVerticalSeam() {
        double[][] distTo = new double[this.height()][this.width()];
        int[][] from = new int[this.height()][this.width()];

        Arrays.fill(distTo[0], 1000.0d);

        for (int y = 0; y < this.height() - 1; y++) {
            for (int x = 0; x < this.width(); x++) {
                this.relax(y, x, distTo, from);
            }
        }

        int[] seam = new int[this.height()];

        int minIndex = 0;
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < this.width(); i++) {
            if (distTo[this.height() - 1][i] < min) {
                min = distTo[this.height() - 1][i];
                minIndex = i;
            }
        }

        for (int i = this.height() - 1; i >= 0; i--) {
            seam[i] = minIndex;
            minIndex = from[i][minIndex];
        }

        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (this.width() <= 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
        if (seam.length != this.height()) {
            throw new IllegalArgumentException();
        }

        Picture newPicture = new Picture(this.width() - 1, this.height());

        for (int row = 0; row < this.height(); row++) {
            int count = 0;
            for (int col = 0; col < this.width(); col++) {
                if (col != seam[row]) {
                    newPicture.set(count, row, picture.get(col, row));
                    count++;
                }
            }
        }

        this.picture = newPicture;
        this.energyMatrix = this.buildEnergy();
    }
}
