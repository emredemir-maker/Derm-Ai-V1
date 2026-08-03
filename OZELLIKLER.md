# Derm-Ai Uygulama Özellikleri Kılavuzu 🚀

**Derm-Ai**, yapay zeka destekli, modern ve kişiselleştirilmiş bir mobil cilt sağlığı asistanıdır. Jetpack Compose, Material Design 3, Room Veritabanı ve Google Gemini API kullanılarak üst düzey bir kullanıcı deneyimi için geliştirilmiştir.

Aşağıda, uygulamanın sunduğu tüm temel modüller, özellikler ve teknik altyapı detaylandırılmıştır:

---

## 1. Kişisel Cilt Profili Kurulumu (Onboarding & Analiz) 🎯
* **Kapsamlı Tanı**: Kullanıcılar uygulamayı ilk açtıklarında cilt tiplerini (Kuru, Yağlı, Karma, Hassas, Normal), ana cilt sorunlarını (akne, kuruluk, leke, gözenek, kırışıklık vb.) ve günlük alışkanlıklarını (su tüketimi, uyku, stres düzeyi) belirten akıllı bir anket doldurur.
* **Akıllı Profil Veritabanı**: Girilen tüm veriler yerel Room veritabanında şifreli ve güvenli bir şekilde saklanır. Kullanıcı dilediği zaman profili güncelleyerek önerileri dinamik olarak yeniden şekillendirebilir.

## 2. İnteraktif Tanıtım Turu (Guided Walkthrough) ✨
* **Kullanıcı Dostu Tasarım**: İlk defa giren kullanıcılar için her ana ekran bileşeninin işlevini adım adım anlatan, pulsasyon efektli ve görsel odaklı interaktif bir "Nasıl Kullanılır?" turu bulunur.
* **Akıllı Durum Yönetimi**: Kullanıcının turu tamamlayıp tamamlamadığı yerel tercihlerde (SharedPreferences) tutulur; böylece her girişte rahatsızlık vermez ancak istendiğinde sağ üstteki buton ile yeniden başlatılabilir.

## 3. Yapay Zeka Destekli Ana Sayfa (Dashboard) 📈
* **Cilt Sağlığı Skor Kartı**: Yapay zeka, kullanıcının profil bilgilerini analiz ederek cildin Nem Dengesi, Bariyer Gücü ve Yağ Seviyesini ölçer ve 100 üzerinden bir **Cilt Sağlığı Skoru** hesaplar.
* **Günlük Akıllı Öneriler & Rutinler**:
  * **Sabah Rutini**: Cildi hazırlayan temizleme, tonik ve güneş koruyucu adımları.
  * **Makyaj Tavsiyesi**: Cilt tipine en uygun, gözenek tıkamayan makyaj önerileri.
* **Bakım Hatırlatıcıları**: Sabah ve akşam rutinlerinin aksatılmaması için özelleştirilebilir yerel bildirimler (alarm saatleri ve akıllı bildirim sistemi).

## 4. Akıllı İçerik Analizi (Ingredient Scanner) 🧪
* **Kamera ile İçindekiler Taraması**: Kullanıcılar satın almak istedikleri veya evlerindeki kozmetik ürünlerin arkasındaki "Ingredients" (İçindekiler) kısmının fotoğrafını çeker veya galeriden yükler.
* **Yapay Zeka İçerik Raporu**: Gemini API, içerik listesini saniyeler içinde analiz ederek:
  * Ürünün **Komadojenik (Gözenek Tıkayıcı)** olup olmadığını,
  * Cilt tipine özel faydalı ve potansiyel tahriş edici maddeleri listeler,
  * Ürüne 10 üzerinden bir **Güvenlik & Uyumluluk Skoru** verir.

## 5. E-Ticaret Fiyat Karşılaştırma Modülü (Price Comparison) 🛍️
* **Piyasa Fiyat Araştırması**: Önerilen bakım ürünleri ve kozmetikler için Türkiye'nin en popüler e-ticaret platformlarındaki (**Trendyol, Hepsiburada, Amazon.tr, Watsons ve Gratis**) güncel fiyat seçenekleri listelenir.
* **Bütçe Dostu AI Tavsiyesi**: En ucuz seçeneği, kargo ücretlerini ve tahmini teslimat sürelerini karşılaştırarak kullanıcıya 1-2 cümlelik akıllı alışveriş önerileri sunar.
* **Doğrudan Yönlendirme**: "Git" butonu aracılığıyla ilgili ürünün platformdaki arama veya ürün detay sayfasına doğrudan web tarayıcı bağlantısı kurulur.

## 6. Fotoğraflı Cilt Günlüğü (Skincare Diary) 📸
* **Görsel Gelişim Takibi**: Kullanıcılar her gün cilt durumlarının fotoğrafını çekerek, cildin günbegün nasıl iyileştiğini görsel bir takvim üzerinden izleyebilir.
* **Günlük Durum Notları**: Fotoğrafa ek olarak o günkü stres, su tüketimi ve özel notlar da günlüğe kaydedilir.

## 7. Derm-Ai Yapay Zeka Danışmanı (AI Chatbot) 💬
* **7/24 Kişisel Dermatoloji Danışmanı**: Kullanıcılar ciltlerinde kızarıklık, ani sivilcelenme veya ürün kullanımı gibi konularda akıllarına takılan tüm soruları sohbet ekranı üzerinden sorabilir.
* **Kişiselleştirilmiş Yanıtlar**: Sohbet motoru, kullanıcının önceden oluşturulmuş cilt profilini otomatik olarak arka planda bildiği için tamamen o kişiye özel, kişiselleştirilmiş ve bütçe dostu tavsiyeler verir.

---

### 🛠️ Teknik Altyapı Özeti
* **Yazılım Dili**: %100 Kotlin
* **Kullanıcı Arayüzü**: Jetpack Compose (Material Design 3 & Edge-to-Edge)
* **Veritabanı & Kalıcılık**: Room DB & Android SharedPreferences
* **Yapay Zeka Servisi**: Google Gemini 1.5 Pro / Flash API
* **Ağ İstemcisi**: Retrofit & Moshi JSON Parser
