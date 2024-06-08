package podongdaeng2.chatgpt

import exposed.model.FoodInfo
import exposed.model.FoodIntake
import exposed.model.MealTimeType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

object SimpleService {
    fun getOpenAiInputString(
        rawFoodIntakeCsvStringData: String,
        rawFoodInfoCsvStringData: String,
        userInfo: String,
        userInput: String,
    ): String {
        val rawFoodIntakeStringList = rawFoodIntakeCsvStringData
            .split("\n")
            .map {
                it.split(""",(?=(?:[^"]*""[^"]*"")*[^"]*$)""".toRegex())
            }
            .drop(2)
            .filter { it.size > 1 }
        val rawFoodInfoStringList = rawFoodInfoCsvStringData
            .split("\n")
            .map {
                it.split(""",(?=(?:[^"]*""[^"]*"")*[^"]*$)""".toRegex())
            }
            .drop(2)
            .filter { it.size > 1 }

        val formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd. a h:mm:ss", Locale.KOREA)

        val foodIntakeList = try {
            rawFoodIntakeStringList.map {
                FoodIntake(
                    foodInfoId = it[10],
                    dataUuid = it[11],
                    name = it[13],
                    mealTimeType = MealTimeType.fromCode(it[3]),
                    amount = it[2].toDouble(),
                    comment = it[6],
                    calorie = it[7].toDouble(),
                    eatenDate = LocalDate.parse(it[5], formatter)
                )
            }
        } catch (e: DateTimeParseException) {
            rawFoodIntakeStringList.map {
                FoodIntake(
                    foodInfoId = it[10],
                    dataUuid = it[11],
                    name = it[13],
                    mealTimeType = MealTimeType.fromCode(it[3]),
                    amount = it[2].toDouble(),
                    comment = it[6],
                    calorie = it[7].toDouble(),
                    eatenDate = LocalDate.parse(it[5], DateTimeFormatter.ofPattern("yyyy. MM. dd. hh:mm:ss", Locale.KOREA))
                )
            }
        }



        val foodInfoList = rawFoodInfoStringList.map {
            FoodInfo(
                dataUuid = it[15],
                name = it[14],
                cholesterol = it[0].toDouble(),
                servingDescription = it[1],
                potassium = it[2].toDouble(),
                sodium = it[3].toDouble(),
                transFat = it[7].toDouble(),
                carbohydrate = it[8].toDouble(),
                metricServingAmount = it[11].toDoubleOrNull(),
                calcium = it[12].toDouble(),
                monosaturatedFat = it[13].toDouble(),
                sugar = it[16].toDouble(),
                saturatedFat = it[17].toDouble(),
                unitCountPerCalorie = it[18].toDoubleOrNull(),
                vitaminA = it[19].toDouble(),
                metricServingUnit = it[20],
                vitaminC = it[21].toDouble(),
                calorie = it[22].toDouble(),
                protein = it[23].toDouble(),
                totalFat = it[24].toDouble(),
                dietaryFiber = it[25].toDouble(),
                iron = it[26].toDouble(),
                polysaturatedFat = it[29].toDouble(),
                defaultNumberOfServingUnit = it[30].toIntOrNull(),
                description = it[31],
            )
        }

        val foodIntakeToFoodInfoList = foodIntakeList.map { foodIntake ->
            foodIntake to foodInfoList.single { it.dataUuid == foodIntake.foodInfoId } // may use hash
        }

        val foodIntakeToFoodInfoListByEatenDate = foodIntakeToFoodInfoList
            .sortedBy { it.first.eatenDate }
            .groupBy { it.first.eatenDate }

        val stringOutputPerDate = foodIntakeToFoodInfoListByEatenDate.map { eachFoodIntakeToFoodInfoListByEatenDate ->
            val eatenDate = eachFoodIntakeToFoodInfoListByEatenDate.key
            val foodIntakeToFoodInfoList = eachFoodIntakeToFoodInfoListByEatenDate.value
            val foodListString = foodIntakeToFoodInfoList
                .groupBy { it.first.mealTimeType }
                .map { (mealTimeType, foodInfoToFoodIntake) ->
                    mealTimeType.toString() + ": " +
                            foodInfoToFoodIntake.joinToString {
                                it.first.name
                            }
                }.joinToString()
            val naturalLanguageNutritionInfo = setDoubleInfoOfFoodIntakeToNaturalLanguage(foodIntakeToFoodInfoList)
            val stringInputForOpenAI = """
                Date: $eatenDate
                user info: $userInfo
                Foods eaten: $foodListString
                user input: $userInput
                
                calorie: ${naturalLanguageNutritionInfo.calorie}
                cholesterol: ${naturalLanguageNutritionInfo.cholesterol}
                potassium: ${naturalLanguageNutritionInfo.potassium}
                sodium: ${naturalLanguageNutritionInfo.sodium}
                trans_fat: ${naturalLanguageNutritionInfo.transFat}
                carbohydrate: ${naturalLanguageNutritionInfo.carbohydrate}
                calcium: ${naturalLanguageNutritionInfo.calcium}
                monosaturated_fat: ${naturalLanguageNutritionInfo.monosaturatedFat}
                saturated_fat: ${naturalLanguageNutritionInfo.saturatedFat}
                sugar: ${naturalLanguageNutritionInfo.sugar}
                vitamin_a: ${naturalLanguageNutritionInfo.vitaminA}
                vitamin_c: ${naturalLanguageNutritionInfo.vitaminC}
                protein: ${naturalLanguageNutritionInfo.protein}
                total_fat: ${naturalLanguageNutritionInfo.totalFat}
                dietary_fiber: ${naturalLanguageNutritionInfo.dietaryFiber}
                iron: ${naturalLanguageNutritionInfo.iron}
                polysaturated_fat: ${naturalLanguageNutritionInfo.polysaturatedFat}
            """.trimIndent()
            stringInputForOpenAI
        }
        return stringOutputPerDate.last() // TODO: Last?
    }

    fun setDoubleInfoOfFoodIntakeToNaturalLanguage(foodIntakeToFoodInfoList: List<Pair<FoodIntake, FoodInfo>>): NaturalLanguageFoodIntake {
        return NaturalLanguageFoodIntake(
            calorie = if (foodIntakeToFoodInfoList.sumOf { it.second.calorie } > 2700) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.calorie } < 2500) {
                "low"
            } else {
                "ok"
            },
            cholesterol = if (foodIntakeToFoodInfoList.sumOf { it.second.cholesterol } > 300) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.cholesterol } < 0) {
                "low"
            } else {
                "ok"
            },
            potassium = if (foodIntakeToFoodInfoList.sumOf { it.second.potassium } > 4700) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.potassium } < 3500) {
                "low"
            } else {
                "ok"
            },
            sodium = if (foodIntakeToFoodInfoList.sumOf { it.second.sodium } > 2300) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.sodium } < 1500) {
                "low"
            } else {
                "ok"
            },
            transFat = if (foodIntakeToFoodInfoList.sumOf { it.second.transFat } > 30) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.transFat } < 0) {
                "low"
            } else {
                "ok"
            },
            carbohydrate = if (foodIntakeToFoodInfoList.sumOf { it.second.carbohydrate } > 400) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.carbohydrate } < 300) {
                "low"
            } else {
                "ok"
            },
            calcium = if (foodIntakeToFoodInfoList.sumOf { it.second.calcium } > 1200) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.calcium } < 800) {
                "low"
            } else {
                "ok"
            },
            monosaturatedFat = if (foodIntakeToFoodInfoList.sumOf { it.second.monosaturatedFat } > 2700) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.monosaturatedFat } < 2500) {
                "low"
            } else {
                "ok"
            },
            saturatedFat = if (foodIntakeToFoodInfoList.sumOf { it.second.saturatedFat } > 50) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.saturatedFat } < 30) {
                "low"
            } else {
                "ok"
            },
            sugar = if (foodIntakeToFoodInfoList.sumOf { it.second.sugar } > 60) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.sugar } < 0) {
                "low"
            } else {
                "ok"
            },
            vitaminA = if (foodIntakeToFoodInfoList.sumOf { it.second.vitaminA } > 900) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.vitaminA } < 600) {
                "low"
            } else {
                "ok"
            },
            vitaminC = if (foodIntakeToFoodInfoList.sumOf { it.second.vitaminC } > 200) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.vitaminC } < 80) {
                "low"
            } else {
                "ok"
            },
            protein = if (foodIntakeToFoodInfoList.sumOf { it.second.protein } > 100) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.protein } < 50) {
                "low"
            } else {
                "ok"
            },
            totalFat = if (foodIntakeToFoodInfoList.sumOf { it.second.totalFat } > 110) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.totalFat } < 40) {
                "low"
            } else {
                "ok"
            },
            dietaryFiber = if (foodIntakeToFoodInfoList.sumOf { it.second.dietaryFiber } > 35) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.dietaryFiber } < 20) {
                "low"
            } else {
                "ok"
            },
            iron = if (foodIntakeToFoodInfoList.sumOf { it.second.iron } > 20) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.iron } < 5) {
                "low"
            } else {
                "ok"
            },
            polysaturatedFat = if (foodIntakeToFoodInfoList.sumOf { it.second.polysaturatedFat } > 30) {
                "high"
            } else if (foodIntakeToFoodInfoList.sumOf { it.second.polysaturatedFat } < 8) {
                "low"
            } else {
                "ok"
            },
        )
    }

    data class NaturalLanguageFoodIntake(
        val calorie: String,
        val carbohydrate: String,
        val protein: String,
        val totalFat: String,
        val sodium: String,
        val transFat: String,
        val calcium: String,
        val cholesterol: String,
        val monosaturatedFat: String,
        val sugar: String,
        val saturatedFat: String,
        val potassium: String,
        val vitaminA: String,
        val vitaminC: String,
        val dietaryFiber: String,
        val iron: String,
        val polysaturatedFat: String,
    )
}