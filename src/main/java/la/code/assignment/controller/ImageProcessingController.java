package la.code.assignment.controller;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import la.code.assignment.dto.ImageComparisonResult;
import la.code.assignment.service.ImageProcessingService;

@RestController
public class ImageProcessingController {
	@Autowired
	private ImageProcessingService imageProcessingService;

	@GetMapping(value = "/open-api")
	@CrossOrigin(origins = "*")
	public void redirectSwagger(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui.html");
	}

	@GetMapping("/image-compare")
	public Set<ImageComparisonResult> performImageComparisons(String s3Bucket, float similarityThreshold) {
		return this.imageProcessingService.performImageComparisons(s3Bucket, similarityThreshold);
	}

	@RequestMapping("/")
	@CrossOrigin(origins = "*")
	public void redirectHome(HttpServletResponse response) throws IOException {
		response.sendRedirect("views/index.html");
	}
}
