package exposed.model

enum class MealType(
    val mealTypeCode: String // code stated in raw csv
) {
    BREAKFAST("100001"),
    BREAKFAST_SNACK("100004"),
    LUNCH("100002"),
    LUNCH_SNACK("100005"),
    DINNER("100003"),
    DINNER_SNACK("100006");

    companion object {
        private val codeToMealTypeMap = entries.associateBy(MealType::mealTypeCode)

        fun fromCode(code: String): MealType? {
            return codeToMealTypeMap[code]
        }
    }
}
