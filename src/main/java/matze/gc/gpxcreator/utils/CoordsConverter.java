package matze.gc.gpxcreator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CoordsConverter {
	private static final Pattern PATTERN_LAT = Pattern.compile(
			"([NS]|)\\s*(\\d+°?|°)(?:\\s*(\\d+)(?:[.,](\\d+)|(?:'|′)?\\s*(\\d+(?:[.,]\\d+)?)(?:''|\"|″)?)?)?",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_LON = Pattern.compile(
			"([WE]|)\\s*(\\d+°?|°)(?:\\s*(\\d+)(?:[.,](\\d+)|(?:'|′)?\\s*(\\d+(?:[.,]\\d+)?)(?:''|\"|″)?)?)?",
			Pattern.CASE_INSENSITIVE);

	double convertToDecimal(boolean latLon, String input) {
		char first = input.charAt(0);
		if (first != 'N' && first != 'S' && first != 'W' && first != 'E') {
			return Double.parseDouble(input);
		}

		String negativePrefix = latLon ? "W" : "S";
		Pattern pattern = latLon ? PATTERN_LAT : PATTERN_LON;
		Matcher matcher = pattern.matcher(input);
		try {
			if (matcher.find()) {
				double sign = matcher.group(1).equalsIgnoreCase(negativePrefix) ? -1.0 : 1.0;
				double degree = Double.parseDouble(
						StringUtils.defaultIfEmpty(
								StringUtils.stripEnd(matcher.group(2), "°"),
								"0"));

				double minutes = 0.0;
				double seconds = 0.0;

				if (matcher.group(3) != null) {
					minutes = Double.parseDouble(matcher.group(3));

					if (matcher.group(4) != null) {
						seconds = Double.parseDouble("0." + matcher.group(4))
								* 60.0;
					} else if (matcher.group(5) != null) {
						seconds = Double.parseDouble(
								matcher.group(5).replace(",", "."));
					}
				}

				return sign * (degree + minutes / 60.0 + seconds / 3600.0);
			}
		} catch (final NumberFormatException ignored) {
			// We might have encountered too large a number. This was not the
			// right way to do it, try another.
		}
		throw new IllegalArgumentException("Illegal coordinates " + input);
	}


}
