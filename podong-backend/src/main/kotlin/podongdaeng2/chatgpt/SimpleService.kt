package podongdaeng2.chatgpt

import exposed.model.FoodInfo
import exposed.model.FoodIntake
import exposed.model.MealTimeType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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


        val foodIntakeList = rawFoodIntakeStringList.map {
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

        val foodIntakeToFoodInfoListByEatenDate = foodIntakeToFoodInfoList.groupBy { it.first.eatenDate }

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
            val stringInputForOpenAI = """
                Date: $eatenDate
                user info: $userInfo
                Foods eaten: $foodListString
                user input: $userInput
                
                calorie: ${foodIntakeToFoodInfoList.sumOf { it.second.calorie }}
                cholesterol: ${foodIntakeToFoodInfoList.sumOf { it.second.cholesterol }}
                potassium: ${foodIntakeToFoodInfoList.sumOf { it.second.potassium }}
                sodium: ${foodIntakeToFoodInfoList.sumOf { it.second.sodium }}
                trans_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.transFat }}
                carbohydrate: ${foodIntakeToFoodInfoList.sumOf { it.second.carbohydrate }}
                calcium: ${foodIntakeToFoodInfoList.sumOf { it.second.calcium }}%
                monosaturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.monosaturatedFat }}
                saturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.saturatedFat }}
                sugar: ${foodIntakeToFoodInfoList.sumOf { it.second.sugar }}
                vitamin_a: ${foodIntakeToFoodInfoList.sumOf { it.second.vitaminA }}%
                vitamin_c: ${foodIntakeToFoodInfoList.sumOf { it.second.vitaminC }}%
                protein: ${foodIntakeToFoodInfoList.sumOf { it.second.protein }}
                total_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.totalFat }}
                dietary_fiber: ${foodIntakeToFoodInfoList.sumOf { it.second.dietaryFiber }}
                iron: ${foodIntakeToFoodInfoList.sumOf { it.second.iron }}%
                polysaturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.polysaturatedFat }}
            """.trimIndent()
            stringInputForOpenAI
        }
        return stringOutputPerDate.joinToString(separator = "\n")
    }
}