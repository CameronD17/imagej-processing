public class ImageData {

	private final Double area;
	private final Long particles;
	private final Double density;

	private ImageData( Builder builder ) {
		this.area = builder.area;
		this.particles = builder.particles;
		this.density = builder.density;
	}

	public Double getArea() {
		return area;
	}

	public Long getParticles() {
		return particles;
	}

	public Double getDensity() {
		return density;
	}

	public static class Builder
	{
		private Double area;
		private Long particles;
		private Double density;

		public Builder area( Double area ) {
			this.area = area;
			return this;
		}

		public Builder particles( Long particles ) {
			this.particles = particles;
			return this;
		}

		public Builder density( Double density ) {
			this.density = density;
			return this;
		}

		public ImageData build() {
			return new ImageData(this);
		}
	}
}
