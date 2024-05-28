package podongdaeng2.test

import exposed.model.FoodInfo
import exposed.model.FoodIntake
import exposed.model.MealTimeType
import org.jetbrains.exposed.sql.Database
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class HealthDataTest {
    init {
        Database.connect(
            "jdbc:mysql://localhost:3306/podongdaeng2", driver = "com.mysql.cj.jdbc.Driver",
            user = "root", password = "1234"
        )
    }

    @Test
    fun healthDataTest() {
        val rawFoodIntakeCsvData = """
            com.samsung.health.food_intake,1148009,3
            unit,pkg_name,amount,meal_type,time_offset,start_time,comment,calorie,deviceuuid,custom,food_info_id,datauuid,create_time,name,update_time
            120001,com.sec.android.app.shealth,1.000000,100001,32400000,2024. 04. 05. 오후 7:40:45,,42.000000,EDuwZTVpg8,,2F210EDC-7D2C-4458-A1DE-70C052D22F12,2561E8A3-7A75-4547-8377-83B84E6C6BF5,2024. 04. 05. 오후 7:41:07,""커피,설탕,프림"",2024. 04. 05. 오후 7:41:07,
            120001,com.sec.android.app.shealth,1.000000,100002,32400000,2024. 04. 05. 오후 12:00:00,,409.000000,EDuwZTVpg8,,5A598034-774B-4041-804B-8D1D7B63F406,B57EF298-BF39-4D7D-82A0-68662DFA6F39,2024. 04. 05. 오후 7:41:26,불고기버거(맥도날드 (McDonald's)),2024. 04. 05. 오후 7:41:26,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 04. 05. 오후 7:42:55,,92.000000,EDuwZTVpg8,,04B08720-3A27-4310-B499-71C13ABDC3D0,B070B96D-C25C-491A-B992-AC91DD0A744E,2024. 04. 05. 오후 7:44:00,콜라 (200ml)(코카콜라),2024. 04. 05. 오후 7:44:00,
            120001,com.sec.android.app.shealth,3.500000,100003,32400000,2024. 04. 05. 오후 7:42:55,,906.000000,EDuwZTVpg8,,A7F7347D-1642-49E6-8191-5DBF5598FEC2,5FEA49BF-8EEE-474A-9453-7FD0C41DBEC3,2024. 04. 05. 오후 7:44:00,양념치킨,2024. 04. 05. 오후 7:44:00,
            120001,com.sec.android.app.shealth,100.000000,100001,32400000,2024. 05. 20. 오전 8:00:00,,57.000000,EDuwZTVpg8,,A288C276-8144-47FF-8A8B-6C625BFF3269,9521BFBB-87FD-4EE0-91B1-03F45C8457DC,2024. 05. 20. 오후 3:07:10,과일샐러드,2024. 05. 20. 오후 3:07:10,
            120001,com.sec.android.app.shealth,2.000000,100001,32400000,2024. 05. 20. 오전 8:00:00,,199.000000,EDuwZTVpg8,,AE84C66B-A90E-48FD-8232-9692D5A82C0C,2943DEEC-A3AE-4D58-8F5A-B411EE1D5F17,2024. 05. 20. 오후 3:07:10,스크램블드 에그,2024. 05. 20. 오후 3:07:10,
            120001,com.sec.android.app.shealth,2.000000,100001,32400000,2024. 05. 20. 오전 8:00:00,,136.000000,EDuwZTVpg8,,1B3691EF-E615-47F6-89AD-FAA3EDEA87D3,845C3E37-FAF7-49BB-AC52-38CE00ABB8C3,2024. 05. 20. 오후 3:07:10,통밀빵 토스트,2024. 05. 20. 오후 3:07:10,
            120001,com.sec.android.app.shealth,1.000000,100001,32400000,2024. 05. 20. 오전 8:00:00,,36.000000,EDuwZTVpg8,,C491CCD7-75FE-42FB-8F7A-36E436173957,979B0B27-A967-497A-8009-FA75CEBC3901,2024. 05. 20. 오후 3:07:10,야채믹스(커클랜드),2024. 05. 20. 오후 3:07:10,
            120001,com.sec.android.app.shealth,1.000000,100004,32400000,2024. 05. 20. 오전 10:00:00,,56.000000,EDuwZTVpg8,,1C85A464-4C29-48B1-AE74-23F56EDEF5A5,7F87BD6C-0BBA-4786-AD8B-F7466C857418,2024. 05. 20. 오후 3:07:46,그릭 요거트(커클랜드),2024. 05. 20. 오후 3:07:46,
            120001,com.sec.android.app.shealth,20.000000,100004,32400000,2024. 05. 20. 오전 10:00:00,,130.000000,EDuwZTVpg8,,19373A3E-5CA8-4C6D-A78F-74FDF291B579,13D8FA61-2525-4216-87A4-6B97A1466833,2024. 05. 20. 오후 3:07:46,호두,2024. 05. 20. 오후 3:07:46,
            120001,com.sec.android.app.shealth,1.000000,100002,32400000,2024. 05. 20. 오후 3:02:05,,240.000000,EDuwZTVpg8,,8C697564-CA32-477E-AF44-FDF20DFB4B1A,AAD8B4E5-237A-49FC-80BF-7B0E6A8D9C23,2024. 05. 20. 오후 3:08:30,로스트 치킨 샐러드(파리바게트),2024. 05. 20. 오후 3:08:30,
            120001,com.sec.android.app.shealth,1.000000,100002,32400000,2024. 05. 20. 오후 3:02:05,,180.000000,EDuwZTVpg8,,546DAE67-1160-4FC8-952E-B7C4E5BA1DBA,AC408484-595C-4FD0-B51A-8C99E3B2AE3D,2024. 05. 20. 오후 3:08:30,Tri-Color Quinoa(Bob's Red Mill),2024. 05. 20. 오후 3:08:30,
            120001,com.sec.android.app.shealth,0.500000,100005,32400000,2024. 05. 20. 오후 3:02:05,,212.000000,EDuwZTVpg8,,1A6225E8-0D57-48D4-9A20-E53E657A93E0,F793928F-8E0F-4BC9-A6FA-C3260EC62F7E,2024. 05. 20. 오후 3:09:25,야채스틱(롯데제과),2024. 05. 20. 오후 3:09:25,
            120001,com.sec.android.app.shealth,100.000000,100003,32400000,2024. 05. 20. 오후 3:02:05,,28.000000,EDuwZTVpg8,,0A3458D8-343C-41DC-A9AC-69641E0D4E86,D883337C-5E5F-4671-94B2-9882A196350F,2024. 05. 20. 오후 3:10:52,""익힌 브로콜리 (냉동, 요리시 지방추가)"",2024. 05. 20. 오후 3:10:52,
            120001,com.sec.android.app.shealth,100.000000,100003,32400000,2024. 05. 20. 오후 3:02:05,,171.000000,EDuwZTVpg8,,5E30CE11-0A6F-41BE-84BA-A29181DCE887,27B57E22-6819-43A9-BCA7-144482690FA0,2024. 05. 20. 오후 3:10:52,연어구이,2024. 05. 20. 오후 3:10:52,
            120001,com.sec.android.app.shealth,100.000000,100003,32400000,2024. 05. 20. 오후 3:02:05,,101.000000,EDuwZTVpg8,,8C12AB76-4298-4AE5-AA6A-FBC894B31790,FE9A05BB-B830-494C-A289-5B9B974B3423,2024. 05. 20. 오후 3:10:52,""고구마 (으깬, 통조림)"",2024. 05. 20. 오후 3:10:52,
            120001,com.sec.android.app.shealth,1.000000,100006,32400000,2024. 05. 20. 오후 3:11:42,,94.000000,EDuwZTVpg8,,2C729947-DCCC-4745-9701-157D30C6818A,FB91A400-9887-420E-8FBC-0D42CF7A506F,2024. 05. 20. 오후 3:11:45,땅콩 버터,2024. 05. 20. 오후 3:11:45,
            120001,com.sec.android.app.shealth,100.000000,100006,32400000,2024. 05. 20. 오후 3:11:42,,52.000000,EDuwZTVpg8,,0187EDFB-5F1E-4B99-B488-CF7F78E1E613,6A21F89C-8535-471F-B49F-8F58A40F1C08,2024. 05. 20. 오후 3:11:45,사과,2024. 05. 20. 오후 3:11:45,
            120001,com.sec.android.app.shealth,0.500000,100003,32400000,2024. 05. 20. 오후 3:02:05,,155.000000,EDuwZTVpg8,,69B4BE5C-6C56-45D1-A726-8FDC6DACE3BF,296A91E0-A767-4799-A0F6-3D1069BCE59E,2024. 05. 20. 오후 3:12:04,공기밥,2024. 05. 20. 오후 3:12:12,
            120001,com.sec.android.app.shealth,1.000000,100001,32400000,2024. 05. 19. 오전 8:00:00,,338.000000,EDuwZTVpg8,,88157F90-2EDB-4563-8934-70306AF743BB,4180056F-E8D7-432C-B6A4-DA75CBAB7B81,2024. 05. 20. 오후 3:14:33,베이컨 치즈 에그 샌드위치(GS25),2024. 05. 20. 오후 3:14:33,
            120001,com.sec.android.app.shealth,1.000000,100001,32400000,2024. 05. 19. 오전 8:00:00,,125.000000,EDuwZTVpg8,,B080FD91-AC47-4987-B890-A212FB3ABCD7,B96F528C-ACBC-4BF7-A586-1DF1687027AA,2024. 05. 20. 오후 3:14:33,해쉬브라운(코스트코),2024. 05. 20. 오후 3:14:33,
            120001,com.sec.android.app.shealth,1.000000,100004,32400000,2024. 05. 19. 오전 10:00:00,,237.000000,EDuwZTVpg8,,40D17B43-E4CC-4A33-A100-F347005EDDB1,8B4F2622-865D-4B72-A24D-AB2069F253AE,2024. 05. 20. 오후 3:14:46,초콜릿 크로와상,2024. 05. 20. 오후 3:14:46,
            120001,com.sec.android.app.shealth,1.000000,100002,32400000,2024. 05. 19. 오후 12:00:00,,859.000000,EDuwZTVpg8,,8A82E140-A830-49CC-890B-8B8CBD6DD214,AC5BA4E0-98E2-4868-B57D-AFD6D9CE6C2F,2024. 05. 20. 오후 3:15:17,딥치즈버거 세트(맘스터치),2024. 05. 20. 오후 3:15:17,
            120001,com.sec.android.app.shealth,1.000000,100005,32400000,2024. 05. 19. 오후 3:00:00,,382.000000,EDuwZTVpg8,,26E364D0-816A-48FF-A19B-3C490EB378D3,E91803A7-C2A0-4085-8E9C-1C51634DD347,2024. 05. 20. 오후 3:15:28,밀크 쉐이크,2024. 05. 20. 오후 3:15:28,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 05. 19. 오후 6:00:00,,85.000000,EDuwZTVpg8,,4A663805-230B-4A73-938D-BDBFC955D0BC,6B63D059-7E0C-4ACB-9288-618788BFCD8F,2024. 05. 20. 오후 3:17:36,사이다 (200ml)(칠성),2024. 05. 20. 오후 3:17:36,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 05. 19. 오후 6:00:00,,186.000000,EDuwZTVpg8,,095CAC56-DA9F-4107-973B-36726F99433F,247227F9-4376-4848-95A6-A41A4098F040,2024. 05. 20. 오후 3:17:36,바닐라 선데이 아이스크림(맥도날드 (McDonald's)),2024. 05. 20. 오후 3:17:36,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 05. 19. 오후 6:00:00,,200.000000,EDuwZTVpg8,,5924BBA1-0B29-406E-BBB7-8877436B0633,E557825B-EAFE-47AE-A69A-7B5F26A67BAB,2024. 05. 20. 오후 3:17:36,Edamame fettuccine(SEAPOINT FARMS),2024. 05. 20. 오후 3:17:36,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 05. 19. 오후 6:00:00,,380.000000,EDuwZTVpg8,,AC262480-C423-41D7-A4FF-5D3CC6BA09A0,5337742F-BD3E-49A5-96FB-03CE272DE1A9,2024. 05. 20. 오후 3:17:36,까르보나라 파스타(생가득),2024. 05. 20. 오후 3:17:36,
            120001,com.sec.android.app.shealth,2.000000,100003,32400000,2024. 05. 19. 오후 6:00:00,,106.000000,EDuwZTVpg8,,EEB6BE04-09BC-4401-BC7C-7A75CC7650A7,3A9AB1DA-B1CC-4451-88D6-DA9D5256E87C,2024. 05. 20. 오후 3:17:36,마늘 빵,2024. 05. 20. 오후 3:17:36,
            120001,com.sec.android.app.shealth,1.500000,100001,32400000,2024. 05. 18. 오전 8:00:00,,429.000000,EDuwZTVpg8,,1A6B985D-CA9B-4BB7-8876-D1A32A0498E7,32C6B049-83A7-4980-846E-DD194D6B7174,2024. 05. 20. 오후 3:18:57,크림치즈베이글,2024. 05. 20. 오후 3:18:57,
            120001,com.sec.android.app.shealth,1.000000,100004,32400000,2024. 05. 18. 오전 10:00:00,,240.000000,EDuwZTVpg8,,F5103EBF-C478-428B-9928-63B9842302CF,87033B3B-6B25-4800-A6C4-2F59A8E794E1,2024. 05. 20. 오후 3:19:09,포카칩 오리지널 (42g)(오리온),2024. 05. 20. 오후 3:19:09,
            120001,com.sec.android.app.shealth,3.000000,100002,32400000,2024. 05. 18. 오후 12:00:00,,711.000000,EDuwZTVpg8,,C4CDEF10-915A-4443-A02F-D7C7A26AD5A2,76A10620-3ED7-47EA-9C51-C6B8B9A76442,2024. 05. 20. 오후 3:19:25,치즈 피자,2024. 05. 20. 오후 3:19:25,
            120001,com.sec.android.app.shealth,1.000000,100005,32400000,2024. 05. 18. 오후 3:00:00,,98.000000,EDuwZTVpg8,,A00F8049-A8E9-44A7-B308-575B81F4961C,8366DAE0-2CDD-4D70-B02D-98D3087D4A15,2024. 05. 20. 오후 3:19:38,초코쿠키,2024. 05. 20. 오후 3:19:38,
            120001,com.sec.android.app.shealth,1.000000,100003,32400000,2024. 05. 18. 오후 6:00:00,,220.000000,EDuwZTVpg8,,DDDD9208-81C7-4F49-BF38-A120F0EBBA6A,8E1189F8-75D6-46A0-8592-CD5AC72AB192,2024. 05. 20. 오후 3:20:03,스파게티,2024. 05. 20. 오후 3:20:03,
            120001,com.sec.android.app.shealth,1.000000,100006,32400000,2024. 05. 18. 오후 9:00:00,,80.000000,EDuwZTVpg8,,E7C55DA3-BB22-4913-AB59-FE1A2810EE33,0AF2C5AB-324C-4011-991B-22765B385EDB,2024. 05. 20. 오후 3:20:19,캔디 바 구이 카라멜 피넛(퀘스트뉴트리션),2024. 05. 20. 오후 3:20:19,
            120001,com.sec.android.app.shealth,1.000000,100006,32400000,2024. 05. 18. 오후 9:00:00,,85.000000,EDuwZTVpg8,,4A663805-230B-4A73-938D-BDBFC955D0BC,C152DB45-65F9-42DA-87F3-B3C9B7373C97,2024. 05. 20. 오후 3:20:32,사이다 (200ml)(칠성),2024. 05. 20. 오후 3:20:32,


        """.trimIndent()

        val rawFoodInfoCsvData = """
            com.samsung.health.food_info,1148009,4
            cholesterol,serving_description,potassium,sodium,create_time,deviceuuid,info_provider,trans_fat,carbohydrate,custom,provider_food_id,metric_serving_amount,calcium,monosaturated_fat,name,datauuid,sugar,saturated_fat,unit_count_per_calorie,vitamin_a,metric_serving_unit,vitamin_c,calorie,protein,total_fat,dietary_fiber,iron,update_time,pkg_name,polysaturated_fat,default_number_of_serving_unit,description
            0.000000,인분,83.000000,5.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,70.699997,,samsungosp-011003,90,5.000000,0.000000,쌀밥,F539778B-7722-4EFD-B964-C20C5EB2A781,0.000000,0.000000,0.284091,1.000000,g,0.000000,316.799988,5.130000,0.090000,0.860000,1.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""317 kcal, 90 g당"",
            0.000000,인분,256.000000,4.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,76.599998,,samsungosp-012011,100,16.000000,0.010000,잡곡밥,116EA3BB-C499-4F8A-93F2-5EE65E5A89AA,0.000000,0.030000,0.283447,1.000000,g,0.000000,352.799988,7.800000,0.640000,3.090000,2.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.060000,1,""353 kcal, 100 g당"",
            0.000000,인분,293.000000,71.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,69.400002,,samsungosp-011005,90,5.000000,0.000000,현미밥,AE980B09-F318-48C1-A384-283639997162,0.000000,0.000000,0.285714,0.000000,g,0.000000,315.000000,6.840000,1.890000,2.970000,1.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""315 kcal, 90 g당"",
            0.000000,인분,180.000000,688.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,2.300000,,samsungosp-141008,60,28.000000,0.000000,배추김치,E7707615-CCB7-4AC0-82BE-DF53D6E04B69,0.000000,0.000000,5.555555,29.000000,g,8.000000,10.800000,1.200000,0.300000,1.800000,0.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""11 kcal, 60 g당"",
            240.000000,인분,385.000000,1252.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,73.900002,,samsungosp-031004,169,42.000000,2.190000,라면,4D5F9066-38FC-4CA0-8F7C-30BF2924562A,0.000000,1.570000,0.328155,159.000000,g,1.000000,515.000000,15.992000,20.599001,3.660000,2.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.800000,1,""515 kcal, 169 g당"",
            79.000000,인분,420.000000,662.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,71.199997,,samsungosp-016001,190,42.000000,2.930000,김밥,DD415C8E-275F-4157-BE53-F21986C280C3,0.000000,1.610000,0.427158,386.000000,g,14.000000,444.799988,10.656000,12.765000,3.310000,3.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,4.410000,1,""445 kcal, 191 g당"",
            0.000000,인분,380.000000,2.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,21.100000,,samsungosp-211016,100,4.000000,0.000000,바나나,C1CF88EB-5073-4847-915F-8188F29B0DD8,0.000000,0.000000,1.250000,7.000000,g,10.000000,80.000000,1.200000,0.200000,1.800000,1.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""80 kcal, 100 g당"",
            7.000000,인분,72.000000,5.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,5.800000,,samsungosp-203017,115,12.000000,0.000000,""커피,설탕,프림"",2F210EDC-7D2C-4458-A1DE-70C052D22F12,0.000000,0.000000,2.686916,18.000000,g,0.000000,42.799999,0.370000,1.935000,0.000000,0.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""43 kcal, 115 g당"",
            0.000000,인분,60.000000,1.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,0.400000,,samsungosp-203018,100,2.000000,0.000000,""커피,원두,블랙"",B369B90F-E89A-4D13-A647-839F86FDBCA9,0.000000,0.000000,33.333332,0.000000,g,0.000000,3.000000,0.100000,0.000000,0.000000,0.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.000000,1,""3 kcal, 100 g당"",
            22.000000,인분,296.000000,110.000000,2024. 04. 05. 오후 7:40:35,EDuwZTVpg8,,0.000000,9.400000,,samsungosp-191005,200,210.000000,1.820000,우유,BB92F022-C64B-49A7-9516-64BF4707A94A,0.000000,4.340000,1.666667,56.000000,g,2.000000,120.000000,6.400000,6.400000,0.000000,0.000000,2024. 04. 05. 오후 7:40:35,com.sec.android.app.shealth,0.220000,1,""120 kcal, 200 g당"",
            30.000000,인분,0.000000,669.000000,2024. 04. 05. 오후 7:41:23,EDuwZTVpg8,,0.300000,44.000000,,fatsecret-3626613,167,0.000000,0.000000,불고기버거(맥도날드 (McDonald's)),5A598034-774B-4041-804B-8D1D7B63F406,14.000000,6.000000,0.408313,0.000000,g,0.000000,409.000000,14.000000,19.000000,0.000000,0.000000,2024. 04. 05. 오후 7:41:23,com.sec.android.app.shealth,0.000000,1,""409 kcal, 1 회 (1 67g)당"",
            0.000000,인분,0.000000,12.000000,2024. 04. 05. 오후 7:44:00,EDuwZTVpg8,,0.000000,23.000000,,fatsecret-6163178,200,0.000000,0.000000,콜라 (200ml)(코카콜라),04B08720-3A27-4310-B499-71C13ABDC3D0,22.000000,0.000000,,0.000000,ml,0.000000,92.000000,0.000000,0.000000,0.000000,0.000000,2024. 04. 05. 오후 7:44:00,com.sec.android.app.shealth,0.000000,1,""92 kcal, 1 컵 (200 ml)당"",
            64.000000,조각,153.000000,208.000000,2024. 04. 05. 오후 7:44:00,EDuwZTVpg8,,0.000000,12.350000,,fatsecret-5493411,90,1.000000,6.801000,양념치킨,A7F7347D-1642-49E6-8191-5DBF5598FEC2,0.090000,4.676000,0.347490,6.000000,g,1.000000,259.000000,14.790000,16.210000,0.500000,5.000000,2024. 04. 05. 오후 7:44:00,com.sec.android.app.shealth,3.498000,1,""6198 kcal, 2152 g당"",
            0.000000,g,141.000000,6.000000,2024. 05. 20. 오후 3:07:10,EDuwZTVpg8,,0.000000,13.160000,,fatsecret-4879970,100,2.000000,0.053000,과일샐러드,A288C276-8144-47FF-8A8B-6C625BFF3269,10.770000,0.647000,1.754386,1.000000,g,40.000000,57.000000,0.670000,0.860000,1.800000,1.000000,2024. 05. 20. 오후 3:07:10,com.sec.android.app.shealth,0.044000,100,""51 kcal, 90 g당"",
            400.000000,달걀,138.000000,211.000000,2024. 05. 20. 오후 3:07:10,EDuwZTVpg8,,0.000000,1.960000,,fatsecret-39866,94,4.000000,5.536000,스크램블드 에그,AE84C66B-A90E-48FD-8232-9692D5A82C0C,1.430000,5.784000,0.472362,23.000000,g,3.000000,199.000000,13.010000,15.210000,0.000000,14.000000,2024. 05. 20. 오후 3:07:10,com.sec.android.app.shealth,1.851000,2,""212 kcal, 100 g당"",
            0.000000,쪽 (레귤러),53.000000,138.000000,2024. 05. 20. 오후 3:07:10,EDuwZTVpg8,,0.000000,12.320000,,fatsecret-3591,24,2.000000,0.449000,통밀빵 토스트,1B3691EF-E615-47F6-89AD-FAA3EDEA87D3,1.440000,0.234000,0.352941,0.000000,g,0.000000,68.000000,2.390000,1.060000,1.300000,5.000000,2024. 05. 20. 오후 3:07:10,com.sec.android.app.shealth,0.239000,1,""282 kcal, 100 g당"",
            0.000000,인분,0.000000,20.000000,2024. 05. 20. 오후 3:07:10,EDuwZTVpg8,,0.000000,7.200000,,fatsecret-30538843,100,0.000000,0.000000,야채믹스(커클랜드),C491CCD7-75FE-42FB-8F7A-36E436173957,2.400000,0.000000,2.777778,0.000000,g,0.000000,36.000000,1.700000,0.000000,0.000000,0.000000,2024. 05. 20. 오후 3:07:10,com.sec.android.app.shealth,0.000000,1,""36 kcal, 100 g당"",
            3.000000,인분,100.000000,30.000000,2024. 05. 20. 오후 3:07:46,EDuwZTVpg8,,0.000000,5.000000,,fatsecret-22415553,100,0.000000,0.000000,그릭 요거트(커클랜드),1C85A464-4C29-48B1-AE74-23F56EDEF5A5,3.000000,0.000000,1.785714,0.000000,g,0.000000,56.000000,9.000000,0.000000,0.000000,0.000000,2024. 05. 20. 오후 3:07:46,com.sec.android.app.shealth,0.000000,1,""56 kcal, 100 g당"",
            0.000000,g,441.000000,2.000000,2024. 05. 20. 오후 3:07:46,EDuwZTVpg8,,0.000000,13.710000,,fatsecret-3379,100,8.000000,8.933000,호두,19373A3E-5CA8-4C6D-A78F-74FDF291B579,2.610000,6.126000,0.152905,0.000000,g,1.000000,654.000000,15.230000,65.210000,6.700000,16.000000,2024. 05. 20. 오후 3:07:46,com.sec.android.app.shealth,47.174000,100,""654 kcal, 100 g당"",
            110.000000,인분,0.000000,560.000000,2024. 05. 20. 오후 3:08:30,EDuwZTVpg8,,0.000000,19.000000,,fatsecret-7499765,205,0.000000,0.000000,로스트 치킨 샐러드(파리바게트),8C697564-CA32-477E-AF44-FDF20DFB4B1A,8.000000,2.700000,0.854167,0.000000,g,0.000000,240.000000,12.000000,10.600000,0.000000,0.000000,2024. 05. 20. 오후 3:08:30,com.sec.android.app.shealth,0.000000,1,""240 kcal, 1 인분 (205 g)당"",
            0.000000,인분,0.000000,0.000000,2024. 05. 20. 오후 3:08:30,EDuwZTVpg8,,0.000000,34.000000,,fatsecret-28740118,48,0.000000,0.000000,Tri-Color Quinoa(Bob's Red Mill),546DAE67-1160-4FC8-952E-B7C4E5BA1DBA,1.000000,0.000000,0.266667,0.000000,g,0.000000,180.000000,6.000000,2.000000,0.000000,0.000000,2024. 05. 20. 오후 3:08:30,com.sec.android.app.shealth,0.000000,1,""1 80 kcal, 1 /4 컵 (4 8g)당"",
            0.000000,인분,0.000000,890.000000,2024. 05. 20. 오후 3:09:25,EDuwZTVpg8,,0.000000,57.000000,,fatsecret-41209663,85,0.000000,0.000000,야채스틱(롯데제과),1A6225E8-0D57-48D4-9A20-E53E657A93E0,9.000000,10.000000,0.200000,0.000000,g,0.000000,425.000000,6.000000,19.000000,0.000000,0.000000,2024. 05. 20. 오후 3:09:25,com.sec.android.app.shealth,0.000000,1,""425 kcal, 1 인분 (85 g)당"",
            0.000000,g,160.000000,219.000000,2024. 05. 20. 오후 3:10:52,EDuwZTVpg8,,0.000000,5.320000,,fatsecret-6011,100,3.000000,0.008000,""익힌 브로콜리 (냉동, 요리시 지방추가)"",0A3458D8-343C-41DC-A9AC-69641E0D4E86,1.430000,0.018000,3.571429,6.000000,g,44.000000,28.000000,3.080000,0.110000,3.000000,3.000000,2024. 05. 20. 오후 3:10:52,com.sec.android.app.shealth,0.055000,100,""52 kcal, 185 g당"",
            62.000000,g,395.000000,467.000000,2024. 05. 20. 오후 3:10:52,EDuwZTVpg8,,0.000000,0.490000,,fatsecret-2059,100,1.000000,2.736000,연어구이,5E30CE11-0A6F-41BE-84BA-A29181DCE887,0.110000,1.312000,0.584795,8.000000,g,2.000000,171.000000,23.970000,7.560000,0.000000,5.000000,2024. 05. 20. 오후 3:10:52,com.sec.android.app.shealth,2.627000,100,""1445 kcal, 845 g당"",
            0.000000,g,210.000000,75.000000,2024. 05. 20. 오후 3:10:52,EDuwZTVpg8,,0.000000,23.190000,,fatsecret-36621,100,2.000000,0.008000,""고구마 (으깬, 통조림)"",8C12AB76-4298-4AE5-AA6A-FBC894B31790,5.450000,0.041000,0.990099,48.000000,g,6.000000,101.000000,1.980000,0.200000,1.700000,7.000000,2024. 05. 20. 오후 3:10:52,com.sec.android.app.shealth,0.084000,100,""101 kcal, 100 g당"",
            0.000000,테이블스푼,104.000000,73.000000,2024. 05. 20. 오후 3:11:45,EDuwZTVpg8,,0.000000,3.130000,,fatsecret-3384,16,1.000000,3.794000,땅콩 버터,2C729947-DCCC-4745-9701-157D30C6818A,1.480000,1.647000,0.170213,0.000000,g,0.000000,94.000000,4.010000,8.060000,1.000000,2.000000,2024. 05. 20. 오후 3:11:45,com.sec.android.app.shealth,2.219000,1,""588 kcal, 100 g당"",
            0.000000,g,107.000000,1.000000,2024. 05. 20. 오후 3:11:45,EDuwZTVpg8,,0.000000,13.810000,,fatsecret-35718,100,0.000000,0.007000,사과,0187EDFB-5F1E-4B99-B488-CF7F78E1E613,10.390000,0.028000,1.923077,0.000000,g,5.000000,52.000000,0.260000,0.170000,2.400000,1.000000,2024. 05. 20. 오후 3:11:45,com.sec.android.app.shealth,0.051000,100,""52 kcal, 100 g당"",
            0.000000,공기,69.000000,0.000000,2024. 05. 20. 오후 3:12:04,EDuwZTVpg8,,0.000000,67.320000,,fatsecret-6202802,210,1.000000,0.361000,공기밥,69B4BE5C-6C56-45D1-A726-8FDC6DACE3BF,0.000000,0.159000,0.677419,0.000000,g,0.000000,310.000000,5.610000,0.850000,0.800000,20.000000,2024. 05. 20. 오후 3:12:04,com.sec.android.app.shealth,0.235000,1,""265 kcal, 179 g당"",
            115.000000,인분,0.000000,930.000000,2024. 05. 20. 오후 3:14:33,EDuwZTVpg8,,0.000000,39.000000,,fatsecret-35195364,170,0.000000,0.000000,베이컨 치즈 에그 샌드위치(GS25),88157F90-2EDB-4563-8934-70306AF743BB,5.000000,3.600000,0.502959,0.000000,g,0.000000,338.000000,14.000000,14.000000,0.000000,0.000000,2024. 05. 20. 오후 3:14:33,com.sec.android.app.shealth,0.000000,1,""338 kcal, 1 인분 (1 70g)당"",
            0.000000,인분,0.000000,210.000000,2024. 05. 20. 오후 3:14:33,EDuwZTVpg8,,0.000000,15.000000,,fatsecret-11056764,60,0.000000,0.000000,해쉬브라운(코스트코),B080FD91-AC47-4987-B890-A212FB3ABCD7,0.000000,0.500000,0.480000,0.000000,g,0.000000,125.000000,1.000000,7.000000,0.000000,0.000000,2024. 05. 20. 오후 3:14:33,com.sec.android.app.shealth,0.000000,1,""1 25 kcal, 1 조각 (60 g)당"",
            34.000000,개,108.000000,383.000000,2024. 05. 20. 오후 3:14:46,EDuwZTVpg8,,0.000000,25.190000,,fatsecret-3535,57,2.000000,3.752000,초콜릿 크로와상,40D17B43-E4CC-4A33-A100-F347005EDDB1,5.830000,7.825000,0.240506,12.000000,g,0.000000,237.000000,4.940000,13.750000,2.300000,11.000000,2024. 05. 20. 오후 3:14:46,com.sec.android.app.shealth,0.650000,1,""416 kcal, 100 g당"",
            0.000000,인분,0.000000,0.000000,2024. 05. 20. 오후 3:15:17,EDuwZTVpg8,,0.000000,1.000000,,fatsecret-28710602,0,0.000000,0.000000,딥치즈버거 세트(맘스터치),8A82E140-A830-49CC-890B-8B8CBD6DD214,0.000000,0.000000,,0.000000,,0.000000,859.000000,1.000000,1.000000,0.000000,0.000000,2024. 05. 20. 오후 3:15:17,com.sec.android.app.shealth,0.000000,1,""859 kcal, 1 회당"",
            45.000000,잔 (300 ml),487.000000,192.000000,2024. 05. 20. 오후 3:15:28,EDuwZTVpg8,,0.000000,56.910000,,fatsecret-924,283,23.000000,3.803000,밀크 쉐이크,26E364D0-816A-48FF-A19B-3C490EB378D3,49.010000,8.385000,0.740838,15.000000,g,1.000000,382.000000,9.030000,13.840000,2.500000,5.000000,2024. 05. 20. 오후 3:15:28,com.sec.android.app.shealth,0.628000,1,""135 kcal, 100 g당"",
            0.000000,인분,0.000000,5.000000,2024. 05. 20. 오후 3:17:36,EDuwZTVpg8,,0.000000,22.000000,,fatsecret-2037942,200,0.000000,0.000000,사이다 (200ml)(칠성),4A663805-230B-4A73-938D-BDBFC955D0BC,16.000000,0.000000,,0.000000,ml,0.000000,85.000000,0.000000,0.000000,0.000000,0.000000,2024. 05. 20. 오후 3:17:36,com.sec.android.app.shealth,0.000000,1,""85 kcal, 1 컵 (200 ml)당"",
            0.000000,인분,0.000000,93.000000,2024. 05. 20. 오후 3:17:36,EDuwZTVpg8,,0.000000,24.000000,,fatsecret-36016621,120,0.000000,0.000000,바닐라 선데이 아이스크림(맥도날드 (McDonald's)),095CAC56-DA9F-4107-973B-36726F99433F,24.000000,4.300000,0.645161,0.000000,g,0.000000,186.000000,4.000000,4.300000,0.000000,0.000000,2024. 05. 20. 오후 3:17:36,com.sec.android.app.shealth,0.000000,1,""1 86 kcal, 1 인분 (1 20g)당"",
            0.000000,인분,0.000000,0.000000,2024. 05. 20. 오후 3:17:36,EDuwZTVpg8,,0.000000,21.000000,,fatsecret-82326092,56,0.000000,0.000000,Edamame fettuccine(SEAPOINT FARMS),5924BBA1-0B29-406E-BBB7-8877436B0633,3.000000,0.500000,0.280000,0.000000,g,0.000000,200.000000,24.000000,3.000000,0.000000,0.000000,2024. 05. 20. 오후 3:17:36,com.sec.android.app.shealth,0.000000,1,""200 kcal, 1 인분 (56 g)당"",
            45.000000,인분,0.000000,560.000000,2024. 05. 20. 오후 3:17:36,EDuwZTVpg8,,0.500000,44.000000,,fatsecret-31821708,230,0.000000,0.000000,까르보나라 파스타(생가득),AC262480-C423-41D7-A4FF-5D3CC6BA09A0,6.000000,10.000000,0.605263,0.000000,g,0.000000,380.000000,11.000000,18.000000,0.000000,0.000000,2024. 05. 20. 오후 3:17:36,com.sec.android.app.shealth,0.000000,1,""380 kcal, 1 인분 (230 g)당"",
            0.000000,작은 슬라이스 (6.5 cm x 5 cm x 2.5 cm),17.000000,98.000000,2024. 05. 20. 오후 3:17:36,EDuwZTVpg8,,0.000000,7.300000,,fatsecret-3469,16,1.000000,0.937000,마늘 빵,EEB6BE04-09BC-4401-BC7C-7A75CC7650A7,0.070000,0.395000,0.301887,2.000000,g,0.000000,53.000000,1.240000,2.040000,0.400000,2.000000,2024. 05. 20. 오후 3:17:36,com.sec.android.app.shealth,0.574000,1,""383 kcal, 116 g당"",
            19.000000,개 (레귤러),87.000000,444.000000,2024. 05. 20. 오후 3:18:57,EDuwZTVpg8,,0.000000,44.650000,,fatsecret-11876565,105,7.000000,2.178000,크림치즈베이글,1A6B985D-CA9B-4BB7-8876-D1A32A0498E7,4.450000,4.188000,0.367133,7.000000,g,1.000000,286.000000,10.090000,7.520000,1.900000,31.000000,2024. 05. 20. 오후 3:18:57,com.sec.android.app.shealth,0.781000,1,""327 kcal, 120 g당"",
            0.000000,인분,0.000000,180.000000,2024. 05. 20. 오후 3:19:09,EDuwZTVpg8,,0.000000,22.000000,,fatsecret-7926237,42,0.000000,0.000000,포카칩 오리지널 (42g)(오리온),F5103EBF-C478-428B-9928-63B9842302CF,0.000000,4.400000,0.175000,0.000000,g,0.000000,240.000000,2.000000,16.000000,0.000000,0.000000,2024. 05. 20. 오후 3:19:09,com.sec.android.app.shealth,0.000000,1,""240 kcal, 1 봉지 (42 g)당"",
            21.000000,조각 (1/8 지름 30 ㎝),138.000000,462.000000,2024. 05. 20. 오후 3:19:25,EDuwZTVpg8,,0.000000,26.080000,,fatsecret-4881,86,14.000000,2.823000,치즈 피자,C4CDEF10-915A-4443-A02F-D7C7A26AD5A2,3.060000,4.304000,0.362869,8.000000,g,0.000000,237.000000,10.600000,10.100000,1.600000,9.000000,2024. 05. 20. 오후 3:19:25,com.sec.android.app.shealth,1.776000,1,""276 kcal, 100 g당"",
            0.000000,개,51.000000,26.000000,2024. 05. 20. 오후 3:19:38,EDuwZTVpg8,,0.000000,16.130000,,fatsecret-3964,25,1.000000,1.292000,초코쿠키,A00F8049-A8E9-44A7-B308-575B81F4961C,11.490000,1.146000,0.255102,3.000000,g,0.000000,98.000000,1.310000,3.480000,0.900000,2.000000,2024. 05. 20. 오후 3:19:38,com.sec.android.app.shealth,0.837000,1,""3827 kcal, 981 g당"",
            0.000000,""컵, 요리된"",63.000000,325.000000,2024. 05. 20. 오후 3:20:03,EDuwZTVpg8,,0.000000,42.950000,,fatsecret-4424,140,1.000000,0.182000,스파게티,DDDD9208-81C7-4F49-BF38-A120F0EBBA6A,0.780000,0.245000,0.636364,0.000000,g,0.000000,220.000000,8.060000,1.290000,2.500000,10.000000,2024. 05. 20. 오후 3:20:03,com.sec.android.app.shealth,0.444000,1,""158 kcal, 101 g당"",
            0.000000,인분,80.000000,60.000000,2024. 05. 20. 오후 3:20:19,EDuwZTVpg8,,0.000000,1.000000,,fatsecret-56435140,21,0.000000,0.000000,캔디 바 구이 카라멜 피넛(퀘스트뉴트리션),E7C55DA3-BB22-4913-AB59-FE1A2810EE33,0.000000,3.000000,0.262500,0.000000,g,0.000000,80.000000,5.000000,6.000000,4.000000,0.000000,2024. 05. 20. 오후 3:20:19,com.sec.android.app.shealth,0.000000,1,""80 kcal, 1 개 (21  g)당"",


        """.trimIndent()

        // split to line then split with comma
        val rawFoodIntakeStringList = rawFoodIntakeCsvData
            .split("\n")
            .map {
                it.split(""",(?=(?:[^"]*""[^"]*"")*[^"]*$)""".toRegex())
            }
            .drop(2)
            .filter { it.size > 1 }
        val rawFoodInfoStringList = rawFoodInfoCsvData
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

        val foodIntakeListByEachMealTimeTypeAndDayMap =
            foodIntakeToFoodInfoList.groupBy { Pair(it.first.eatenDate, it.first.mealTimeType) }

        val finalStringInputForOpenAI =
            foodIntakeListByEachMealTimeTypeAndDayMap.map { foodIntakeListByEachMealTimeTypeAndDay ->
                val (eatenDate, mealTimeType) = foodIntakeListByEachMealTimeTypeAndDay.key
                val eachFoodIntakeToFoodInfoList = foodIntakeListByEachMealTimeTypeAndDay.value
                val userInfo = "172cm, 80kg, male\n" // TODO
                val userInput = "단백질이 많은 식단으로 먹고 싶어"
                val foodListString = eachFoodIntakeToFoodInfoList.map {
                    it.second.name
                }.joinToString(postfix = "\n")
                val stringInputForOpenAI = """
                
                Date: $eatenDate, Type: $mealTimeType
                user info: $userInfo
                Foods eaten: $foodListString
                user input: $userInput
                
                calorie: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.calorie }}
                cholesterol: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.cholesterol }}
                potassium: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.potassium }}
                sodium: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.sodium }}
                trans_fat: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.transFat }}
                carbohydrate: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.carbohydrate }}
                calcium: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.calcium }}
                monosaturated_fat: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.monosaturatedFat }}
                saturated_fat: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.saturatedFat }}
                sugar: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.sugar }}
                vitamin_a: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.vitaminA }}
                vitamin_c: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.vitaminC }}
                protein: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.protein }}
                total_fat: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.totalFat }}
                dietary_fiber: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.dietaryFiber }}
                iron: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.iron }}
                polysaturated_fat: ${eachFoodIntakeToFoodInfoList.sumOf { it.second.polysaturatedFat }}
                
            """.trimIndent()
                stringInputForOpenAI
            }

        val foodIntakeToFoodInfoListByEatenDate = foodIntakeToFoodInfoList.groupBy { it.first.eatenDate }
        val stringOutputPerDate = foodIntakeToFoodInfoListByEatenDate.map { eachFoodIntakeToFoodInfoListByEatenDate ->
            val eatenDate = eachFoodIntakeToFoodInfoListByEatenDate.key
            val foodIntakeToFoodInfoList = eachFoodIntakeToFoodInfoListByEatenDate.value
            val userInfo = "172cm, 80kg, male" // TODO
            val userInput = "단백질이 많은 식단으로 먹고 싶어"
            val foodListString = foodIntakeToFoodInfoList
                .groupBy { it.first.mealTimeType }
                .map { (mealTimeType, foodInfoToFoodIntake) ->
                    mealTimeType.toString() + ": " +
                    foodInfoToFoodIntake.joinToString {
                        it.first.name
                    }
                }.joinToString(postfix = "\n")
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
                calcium: ${foodIntakeToFoodInfoList.sumOf { it.second.calcium }}
                monosaturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.monosaturatedFat }}
                saturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.saturatedFat }}
                sugar: ${foodIntakeToFoodInfoList.sumOf { it.second.sugar }}
                vitamin_a: ${foodIntakeToFoodInfoList.sumOf { it.second.vitaminA }}
                vitamin_c: ${foodIntakeToFoodInfoList.sumOf { it.second.vitaminC }}
                protein: ${foodIntakeToFoodInfoList.sumOf { it.second.protein }}
                total_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.totalFat }}
                dietary_fiber: ${foodIntakeToFoodInfoList.sumOf { it.second.dietaryFiber }}
                iron: ${foodIntakeToFoodInfoList.sumOf { it.second.iron }}
                polysaturated_fat: ${foodIntakeToFoodInfoList.sumOf { it.second.polysaturatedFat }}
            """.trimIndent()
            stringInputForOpenAI
        }

//        println(finalStringInputForOpenAI)
        println(stringOutputPerDate.joinToString(separator = "\n"))
    }
}
