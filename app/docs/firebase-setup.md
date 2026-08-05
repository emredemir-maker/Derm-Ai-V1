# Firebase ve AI Logic Kurulum Rehberi (Derm-Ai)

Bu belge, Derm-Ai uygulamasında Firebase AI Logic ve App Check mimarisini canlı ortamlarda yapılandırmak için izlenmesi gereken adımları açıklamaktadır.

## 1. Firebase Projesi Oluşturma
1. [Firebase Console](https://console.firebase.google.com/) adresine gidin.
2. Yeni bir proje oluşturun veya mevcut bir projeyi seçin.

## 2. Android Uygulamasını Ekleme
1. Firebase Console üzerinden projeye bir Android uygulaması ekleyin.
2. Paket Adı (Package Name) olarak tam olarak şunu girin:
   `com.aistudio.ciltanalizvebakim.shskdj`
3. Proje imza sertifikası SHA-256 anahtarlarını ekleyin (özellikle Google Play / Release imzalama için).

## 3. google-services.json Dosyasını İndirme
1. Yapılandırma sonrasında indirilen `google-services.json` dosyasını alın.
2. Dosyayı projenin `app/` dizini altına yerleştirin (`app/google-services.json`).
3. **Önemli:** `google-services.json` dosyası kesinlikle Git deposuna (`.gitignore`) eklenmemelidir.

## 4. Firebase AI Logic Kurulumu
1. Firebase Console > **AI Services** veya **AI Logic** bölümüne gidin.
2. Sağlayıcı (Provider) olarak **Gemini Developer API** (veya desteklenen Gemini model uç noktaları) seçeneğini etkinleştirin.

## 5. App Check Yapılandırması
1. Firebase Console > **App Check** bölümüne gidin.
2. Android uygulamanız için **Play Integrity** sağlayıcısını etkinleştirin.
3. Debug (Hata ayıklama) testleri için Debug App Check sağlayıcı token'ını Firebase Console'da kaydedin.

## 6. Yayın (Release) ve Güvenlik Notları
- Yayın öncesinde Firebase AI Logic enforcement (zorunlu kılma) modunu aktif hale getirebilirsiniz.
- API anahtarları kaynak kodda veya `BuildConfig` içerisinde saklanmaz; tüm istekler Firebase App Check kimlik doğrulaması üzerinden güvenli bir şekilde sunulur.
