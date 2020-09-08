package la.code.assignment.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import la.code.assignment.dto.ImageComparisonResult;
import la.code.assignment.service.ImageProcessingService;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private static Logger LOG = LoggerFactory.getLogger(ImageProcessingServiceImpl.class);

	private AmazonRekognition rekognitionClient;

	private AmazonS3 s3Client;

	@PostConstruct
	public void initService() {
		LOG.debug("begin initService()...");

		rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
		s3Client = AmazonS3ClientBuilder.defaultClient();

		LOG.debug("begin initService()...");
	}

	@PreDestroy
	public void shutdown() {
		rekognitionClient.shutdown();
		s3Client.shutdown();
	}

	public ImageComparisonResult performImageComparison(String sourceImageBucket, String sourceImageId,
			String targetImageBucket, String targetImageId, float similarityThreshold) {
		CompareFacesRequest cfRequest = new CompareFacesRequest();
		cfRequest.withSimilarityThreshold(similarityThreshold);

		cfRequest.withSourceImage(
				new Image().withS3Object(new S3Object().withBucket(sourceImageBucket).withName(sourceImageId)));

		cfRequest.withTargetImage(
				new Image().withS3Object(new S3Object().withBucket(targetImageBucket).withName(targetImageId)));

		CompareFacesResult cfResults = rekognitionClient.compareFaces(cfRequest);

		ImageComparisonResult comparisonResult = new ImageComparisonResult();
		comparisonResult.setSourceImageS3Bucket(sourceImageBucket);
		comparisonResult.setSourceImage(sourceImageId);
		comparisonResult.setTargetImageS3Bucket(targetImageBucket);
		comparisonResult.setTargetImage(targetImageId);
		comparisonResult.setConfidenceLevel(0f);

		for (CompareFacesMatch fm : cfResults.getFaceMatches()) {
			float confidence = fm.getFace().getConfidence();
			if (confidence > comparisonResult.getConfidenceLevel()) {
				comparisonResult.setConfidenceLevel(confidence);
				comparisonResult.setSimilaryLevel(fm.getSimilarity());
			}
		}

		return comparisonResult;
	}

	public Set<ImageComparisonResult> performImageComparisons(String aImageBucket, float aSimilarityThreshold) {
		Set<ImageComparisonResult> comparisonResults = new LinkedHashSet<>();

		ObjectListing objListing = s3Client.listObjects(aImageBucket);
		List<S3ObjectSummary> objSummaries = objListing.getObjectSummaries();

		for (int fNdx = 0; fNdx < objSummaries.size() - 1; fNdx++) {
			for (int sNdx = fNdx + 1; sNdx < objSummaries.size(); sNdx++) {
				ImageComparisonResult imgComparisonResult = performImageComparison(aImageBucket,
						objSummaries.get(fNdx).getKey(), aImageBucket, objSummaries.get(sNdx).getKey(),
						aSimilarityThreshold);

				comparisonResults.add(imgComparisonResult);
			}
		}

		return comparisonResults;
	}

}
