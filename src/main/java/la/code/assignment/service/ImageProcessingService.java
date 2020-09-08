package la.code.assignment.service;

import java.util.Set;

import la.code.assignment.dto.ImageComparisonResult;

public interface ImageProcessingService {
	ImageComparisonResult performImageComparison(String aSourceImageBucket, String aSourceImageId,
			String aTargetImageBucket, String aTargetImageId, float aSimilarityThreshold);

	Set<ImageComparisonResult> performImageComparisons(String aImageBucket, float aSimilarityThreshold);
}
