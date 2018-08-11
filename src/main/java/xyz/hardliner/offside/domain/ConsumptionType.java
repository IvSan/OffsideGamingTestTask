package xyz.hardliner.offside.domain;

public enum ConsumptionType {
	GAS, COLD_WATER, HOT_WATER;

	public static ConsumptionType get(String type) {
		if (GAS.name().equalsIgnoreCase(type)) {
			return GAS;
		} else if (COLD_WATER.name().equalsIgnoreCase(type)) {
			return COLD_WATER;
		} else if (HOT_WATER.name().equalsIgnoreCase(type)) {
			return HOT_WATER;
		}
		throw new IllegalArgumentException("Unsupported consumption type '" + type + "'");
	}
}
