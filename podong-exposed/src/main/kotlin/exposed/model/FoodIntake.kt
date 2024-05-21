package exposed.model

data class FoodIntake(
    val foodInfoId: String,
    val dataUuid: String,
    val name: String,
    val mealType: MealType?, // 100001: 아침, 100004: 아침 간식, 100003: 저녁 ...
    val amount: Double?,
    val comment: String?,
    val calorie: Double?,
)