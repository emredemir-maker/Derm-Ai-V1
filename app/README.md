# Veritabanı Değişikliği ve Migration Rehberi

Bu proje Room veritabanı kullanmaktadır ve veritabanı şeması `app/schemas/` altında sürüm bazlı saklanmaktadır.

## Veritabanı Şeması Değişikliği Adımları

1. **Sürüm Artırımı:** Entity sınıflarında (örn. `Entities.kt`) herhangi bir tablo veya sütun ekleme/çıkarma/değişiklik yapıldığında, `AppDatabase.kt` içerisindeki `@Database(version = ...)` değeri mutlaka 1 artırılmalıdır.
2. **Açık Migration Yazımı:** Sürüm geçişleri için `Migration(startVersion, endVersion)` nesnesi oluşturulmalı ve `AppDatabase.getDatabase` içerisinde `addMigrations(...)` ile eklenmelidir.
3. **Migration Testi:** Eski şema ve yeni şema JSON dosyaları karşılaştırılarak migration adımlarının veri kaybına yol açmadığı test edilmelidir.
4. **Destructive Migration Kısıtlaması:** Release (üretim) derlemelerinde `fallbackToDestructiveMigration()` **kesinlikle kullanılmaz**. Yalnızca `BuildConfig.DEBUG == true` durumunda geliştirme kolaylığı için aktif tutulur.
