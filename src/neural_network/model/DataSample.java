package neural_network.model;

public class DataSample {
    private double[] pixels;
    private double[] label;
    
    public DataSample(double[] pixels, double[] label) {
        this.setPixels(pixels);
        this.setLabel(label);
    }

	public double[] getLabel() {
		return label;
	}

	public void setLabel(double[] label) {
		this.label = label;
	}

	public double[] getPixels() {
		return pixels;
	}

	public void setPixels(double[] pixels) {
		this.pixels = pixels;
	}
}