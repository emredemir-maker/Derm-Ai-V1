package com.example.data.database

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object DefaultRecommendations {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, ProductSuggestion::class.java)
    private val listAdapter = moshi.adapter<List<ProductSuggestion>>(listType)

    fun getDefaults(skinType: String): SkinTypeRecommendation {
        val (creams, makeup, tips) = when (skinType) {
            "Kuru" -> Triple(
                listOf(
                    ProductSuggestion(
                        name = "Ultra Yoğun Ceramide Nemlendirici",
                        category = "Nemlendirici",
                        activeIngredients = "Seramid NP, Hiyalüronik Asit, Shea Yağı",
                        description = "Cilt bariyerini derinlemesine onarır, nemsizlikten kaynaklanan gerginliği anında yatıştırır ve 48 saat nem koruması sağlar.",
                        usageTip = "Temiz cilde sabah ve akşam ince bir tabaka halinde dairesel hareketlerle uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Hiyalüronik Asit Nem Serumu",
                        category = "Serum",
                        activeIngredients = "3'lü Hiyalüronik Asit Kompleksi, B5 Vitamini (Panthenol)",
                        description = "Nemi alt katmanlara hapsederek cilde dolgunluk kazandırır ve nemsizlik çizgilerini gözle görülür şekilde azaltır.",
                        usageTip = "Nemlendirici krem öncesinde, hafif nemli cilde 3-4 damla uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Yulaf Özlü Yatıştırıcı Temizleme Sütü",
                        category = "Temizleyici",
                        activeIngredients = "Koloidal Yulaf Ezmesi, Tatlı Badem Yağı",
                        description = "Cildi kurutmadan, doğal nem dengesini koruyarak nazikçe temizler ve gözenekleri kirden tamamen arındırır.",
                        usageTip = "Kuru cilde masaj yaparak uygulayın, ardından pamukla veya ılık suyla durulayın."
                    )
                ),
                listOf(
                    ProductSuggestion(
                        name = "Nemli Bitişli Işıltılı Fondöten",
                        category = "Fondöten",
                        activeIngredients = "Gliserin, Skualen, SPF 30",
                        description = "Kuru ve pullanan bölgeleri belli etmeden cilde doğal, canlı ve ıslak bitişli bir görünüm kazandırır.",
                        usageTip = "Nemli bir makyaj süngeri yardımıyla tampon hareketlerle uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Serum Katkılı Yoğun Kapatıcı",
                        category = "Kapatıcı",
                        activeIngredients = "Hiyalüronik Asit, E Vitamini",
                        description = "Göz altlarındaki kuruluk çizgilerine dolmadan morlukları kapatır ve gün boyu taze bir görünüm sunar.",
                        usageTip = "Yumuşak fırça veya parmak uçlarıyla hafifçe dağıtarak uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Işıltı Veren Nemlendirici Makyaj Bazı",
                        category = "Astar (Primer)",
                        activeIngredients = "Niasinamid, Gül Suyu",
                        description = "Makyajın pullanmasını önler, cilde içten gelen doğal bir ışıltı sağlar.",
                        usageTip = "Makyaja başlamadan önce tüm yüze nemlendirici gibi yedirin."
                    )
                ),
                "Alkol içeren toniklerden uzak durun. Duştan hemen sonra, cilt henüz nemliyken bakım ürünlerinizi uygulayarak nemi hapsedin."
            )
            "Yağlı" -> Triple(
                listOf(
                    ProductSuggestion(
                        name = "Su Bazlı Salisilik Asit Nemlendirici Jel",
                        category = "Nemlendirici",
                        activeIngredients = "Salisilik Asit (BHA), Çay Ağacı Yağı, Cadı Fındığı",
                        description = "Cildi yağlandırmadan nemlendirir, gün boyu parlamayı kontrol eder ve gözenekleri arındırır.",
                        usageTip = "Temizlenmiş cilde sabah ve akşam yağsız jel formülüyle dairesel masajla uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Niasinamid & Çinko Sebum Serumu",
                        category = "Serum",
                        activeIngredients = "%10 Niasinamid, %1 Çinko PCA",
                        description = "Aşırı yağ üretimini dengeler, gözenekleri belirgin şekilde sıkılaştırır ve leke görünümünü azaltır.",
                        usageTip = "Günde bir kez, akşamları temiz cilde uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Arındırıcı Köpüren Jel Temizleyici",
                        category = "Temizleyici",
                        activeIngredients = "Salisilik Asit, Çinko, Sülfatsız Sürfaktanlar",
                        description = "Cildin sebum dengesini bozmadan gözenekleri kirden ve fazla yağdan tamamen arındırır.",
                        usageTip = "Elde köpürterek ıslak cilde masajla uygulayın ve bol suyla yıkayın."
                    )
                ),
                listOf(
                    ProductSuggestion(
                        name = "Mat Bitişli Yağsız Gözenek Fondöteni",
                        category = "Fondöten",
                        activeIngredients = "Doğal Kil Mineralleri, Silis",
                        description = "Yağlanma ve parlamayı önler, gün boyu kadifemsi pürüzsüz mat bir ten sunar.",
                        usageTip = "Fırça veya makyaj süngeriyle uygulayıp transparan pudrayla sabitleyin."
                    ),
                    ProductSuggestion(
                        name = "Yüksek Kapatıcılıklı Mat Kapatıcı",
                        category = "Kapatıcı",
                        activeIngredients = "Çay Ağacı Yağı, Salisilik Asit",
                        description = "Sivilce ve lekeleri mükemmel şekilde kapatırken içeriğindeki aktiflerle iyileşmelerine yardımcı olur.",
                        usageTip = "Lekelerin üzerine noktasal uygulayın ve kenarlarını hafifçe dağıtın."
                    ),
                    ProductSuggestion(
                        name = "Gözenek Sıkılaştırıcı Mat Astar",
                        category = "Astar (Primer)",
                        activeIngredients = "Silika, Çinko PCA",
                        description = "Gözenek görünümünü sıfırlayarak makyajın kusursuz durmasını ve parlamamasını sağlar.",
                        usageTip = "Özellikle T-bölgesine makyaj öncesi hafif tampon hareketlerle yedirin."
                    )
                ),
                "Gözeneklerinizi tıkamayan 'non-comedogenic' ibareli hafif su bazlı ürünleri tercih edin. Haftada 1-2 kez kil maskesi uygulamak fazla yağı emmekte harikadır."
            )
            "Karma" -> Triple(
                listOf(
                    ProductSuggestion(
                        name = "Dengeleyici Jel-Krem Nemlendirici",
                        category = "Nemlendirici",
                        activeIngredients = "Skualen, Centella Asiatica, Yeşil Çay Özü",
                        description = "Yanakları kurutmadan derinlemesine nemlendirirken, T-bölgesindeki sebum salgısını dengeler.",
                        usageTip = "Tüm yüze eşit şekilde uygulayın; kuru yanak bölgelerine bir kat daha geçebilirsiniz."
                    ),
                    ProductSuggestion(
                        name = "Gözenek ve Sebum Dengeleyici Serum",
                        category = "Serum",
                        activeIngredients = "Niasinamid (B3), Cadı Fındığı (Witch Hazel)",
                        description = "T-bölgesindeki parlamayı önlerken yanakların nem dengesini korumasına yardımcı olur.",
                        usageTip = "Tüm yüze veya sadece parlamaya meyilli bölgelere sabahları uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Nazik Arındırıcı Köpüren Temizleyici",
                        category = "Temizleyici",
                        activeIngredients = "Centella, Amino Asitler",
                        description = "Cildi kurutmadan T-bölgesini derinlemesine arındırır ve cildin genel nem dengesini korur.",
                        usageTip = "Sabah ve akşam ıslak cilde dairesel masajla uygulayıp durulayın."
                    )
                ),
                listOf(
                    ProductSuggestion(
                        name = "Yarı Mat Doğal Bitişli Fondöten",
                        category = "Fondöten",
                        activeIngredients = "Niasinamid, Hiyalüronik Asit",
                        description = "Karma ciltler için ideal dengede ne çok mat ne çok ıslak, pürüzsüz doğal bir görünüm sağlar.",
                        usageTip = "T-bölgesinden dışa doğru hafif nemli süngerle uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Doğal Bitişli Likit Kapatıcı",
                        category = "Kapatıcı",
                        activeIngredients = "E Vitamini, Aloe Vera",
                        description = "Göz altlarını kurutmadan, gün boyu çizgilenme yapmadan pürüzleri gizler.",
                        usageTip = "Göz altlarına ve burun kenarlarına uygulayıp parmak ucunuzla dağıtın."
                    ),
                    ProductSuggestion(
                        name = "Bölgesel Matlaştırıcı Makyaj Bazı",
                        category = "Astar (Primer)",
                        activeIngredients = "Silika, Vitaminler",
                        description = "Sadece T-bölgesine uygulanarak o bölgedeki parlamayı önler ve makyajın kalıcılığını artırır.",
                        usageTip = "Sadece burun, alın ve çene bölgesine lokal olarak uygulayın."
                    )
                ),
                "Karma ciltler için çoklu maskeleme (multi-masking) yapın: T-bölgesine kil maskesi uygularken, kuru yanaklarınıza yoğun nem maskesi uygulayın."
            )
            "Hassas" -> Triple(
                listOf(
                    ProductSuggestion(
                        name = "Sakinleştirici Bariyer Koruyucu Krem",
                        category = "Nemlendirici",
                        activeIngredients = "Centella Asiatica (Cica), Seramid Kompleksi, Allantoin",
                        description = "Kızarıklığı ve kaşıntıyı anında hafifletir, zarar görmüş cilt bariyerini onarır.",
                        usageTip = "Sabah ve akşam, hassaslaşmış bölgelere masaj yapmadan tamponlayarak uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Bariyer Güçlendirici Panthenol Serum",
                        category = "Serum",
                        activeIngredients = "%5 Panthenol, Madecassoside, Meyan Kökü Özü",
                        description = "Cildi dış etkenlere karşı korur, toleransını artırır ve tahrişleri yatıştırır.",
                        usageTip = "Temiz cilde birkaç damla uygulayıp hafif dokunuşlarla emilmesini sağlayın."
                    ),
                    ProductSuggestion(
                        name = "Hassas Köpük Temizleyici (Sülfatsız)",
                        category = "Temizleyici",
                        activeIngredients = "Aloe Vera, Papatya Özü",
                        description = "Parfüm ve sülfat içermeyen formülüyle cildi tahriş etmeden, kızartmadan nazikçe temizler.",
                        usageTip = "Köpüğü cilde dairesel hareketlerle yumuşakça yayın ve ılık suyla durulayın."
                    )
                ),
                listOf(
                    ProductSuggestion(
                        name = "Hifif Kapatıcılıklı Mineral CC Krem",
                        category = "Fondöten",
                        activeIngredients = "Fiziksel SPF filtreleri, Çinko Oksit, Cica",
                        description = "Cildi yormadan hafif bir kapatıcılık sağlar, kızarıklıkları nötrler ve güneşten korur.",
                        usageTip = "Makyaj süngeri veya temiz parmaklarla nemlendirici gibi yayın."
                    ),
                    ProductSuggestion(
                        name = "Yatıştırıcı Yeşil Kapatıcı",
                        category = "Kapatıcı",
                        activeIngredients = "Centella Asiatica, Yeşil Çay",
                        description = "Kızarıklıkları (akne izleri, kılcal damarlar) yeşil pigmentleri sayesinde mükemmel şekilde gizler.",
                        usageTip = "Kızarık bölgelere noktasal uygulayın, ardından ten rengi kapatıcınızla bütünleştirin."
                    ),
                    ProductSuggestion(
                        name = "Parfümsüz Yatıştırıcı Baz",
                        category = "Astar (Primer)",
                        activeIngredients = "Aloe Vera, Papatya Suyu",
                        description = "Tahriş olmuş cildi sakinleştirir, kozmetiklerin cildi tahriş etmesini önler.",
                        usageTip = "Makyaj öncesi tüm yüze uygulayarak koruyucu bir bariyer oluşturun."
                    )
                ),
                "Yeni bir ürünü tüm yüzünüze uygulamadan önce mutlaka kulak arkasında patch test yapın. Parfüm, alkol ve uçucu yağlardan uzak durun."
            )
            else -> Triple( // Normal
                listOf(
                    ProductSuggestion(
                        name = "Işıltı Veren Günlük Nemlendirici",
                        category = "Nemlendirici",
                        activeIngredients = "C Vitamini, Skualen, Niasinamid",
                        description = "Cildin doğal nemini ve dengesini korur, gün boyu sağlıklı, taze ve ışıltılı bir görünüm sağlar.",
                        usageTip = "Her sabah ve akşam temiz yüze dairesel masajla uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Antioksidan Vitamin Serumu",
                        category = "Serum",
                        activeIngredients = "C Vitamini, E Vitamini, Ferulik Asit",
                        description = "Cilt tonunu eşitler, ince çizgilere karşı korur ve cildin doğal kolajen üretimini destekler.",
                        usageTip = "Her sabah temiz cilde güneş kreminden önce 3-4 damla uygulayın."
                    ),
                    ProductSuggestion(
                        name = "Nazik Arındırıcı Köpük Jel",
                        category = "Temizleyici",
                        activeIngredients = "Hiyalüronik Asit, Amino Asitler",
                        description = "Cildin pH dengesini bozmadan gözenekleri kirden arındırır, ferahlık ve yumuşaklık verir.",
                        usageTip = "Günde iki kez ıslak yüzünüze masajla uygulayıp ılık suyla durulayın."
                    )
                ),
                listOf(
                    ProductSuggestion(
                        name = "Doğal Işıltılı Saten Fondöten",
                        category = "Fondöten",
                        activeIngredients = "Gliserin, Vitamin Kompleksi",
                        description = "Cildin doğal güzelliğini ortaya çıkaran, maske etkisi yaratmayan, saten bitişli pürüzsüz bir ten sunar.",
                        usageTip = "Nemli sünger veya fırça yardımıyla tüm yüze eşit dağıtın."
                    ),
                    ProductSuggestion(
                        name = "Aydınlık Bitişli Likit Kapatıcı",
                        category = "Kapatıcı",
                        activeIngredients = "E Vitamini, Aydınlatıcı Mineraller",
                        description = "Göz çevresini aydınlatarak yorgunluk izlerini siler, kusurları gizler.",
                        usageTip = "Göz pınarına ve dış köşesine uygulayıp yukarı doğru dağıtarak lift etkisi yaratın."
                    ),
                    ProductSuggestion(
                        name = "Nemlendirici ve Gözenek Pürüzsüzleştirici Baz",
                        category = "Astar (Primer)",
                        activeIngredients = "Hiyalüronik Asit, Niasinamid",
                        description = "Makyajın kalıcılığını mükemmel seviyeye çıkarırken cildin taze ve canlı görünmesini destekler.",
                        usageTip = "Tüm yüze ince bir kat halinde uygulayın."
                    )
                ),
                "Cildinizin sağlıklı dengesini korumak için yaz-kış her gün en az SPF 50 içeren geniş spektrumlu bir güneş kremi kullanmayı ihmal etmeyin."
            )
        }

        val creamsJson = listAdapter.toJson(creams)
        val makeupJson = listAdapter.toJson(makeup)

        return SkinTypeRecommendation(
            skinType = skinType,
            creamSuggestionsJson = creamsJson,
            makeupSuggestionsJson = makeupJson,
            generalTips = tips
        )
    }
}
