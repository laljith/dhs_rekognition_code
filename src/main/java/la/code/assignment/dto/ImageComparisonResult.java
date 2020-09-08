package la.code.assignment.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImageComparisonResult implements Serializable {
	private static final long serialVersionUID = 6129055236139969731L;

	private String sourceImageS3Bucket;
	private String sourceImage;
	private String targetImageS3Bucket;
	private String targetImage;

	private float confidenceLevel;
	private float similaryLevel;
}
