package exposed.model

enum class MealTimeType(
    val mealTypeCode: String // code stated in raw csv
) {
    BREAKFAST("100001"),
    BREAKFAST_SNACK("100004"),
    LUNCH("100002"),
    LUNCH_SNACK("100005"),
    DINNER("100003"),
    DINNER_SNACK("100006");

    companion object {
        private val codeToMealTypeMap = entries.associateBy(MealTimeType::mealTypeCode)

        fun fromCode(code: String): MealTimeType? {
            return codeToMealTypeMap[code]
        }
    }
}
