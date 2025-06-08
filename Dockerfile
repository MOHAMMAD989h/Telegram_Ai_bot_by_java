# --- مرحله ۱: ساخت پروژه (Builder) ---
# از یک ایمیج که Maven و JDK نسخه ۱۷ را دارد به عنوان پایه استفاده می‌کنیم
# و به این مرحله یک نام (builder) می‌دهیم
FROM maven:3.8.5-openjdk-17 AS builder

# یک پوشه کاری برای برنامه خود ایجاد می‌کنیم
WORKDIR /app

# ابتدا فقط فایل pom.xml را کپی می‌کنیم. این کار برای بهینه‌سازی کش لایه‌های داکر است
COPY pom.xml .

# تمام نیازمندی‌ها (dependencies) را دانلود می‌کنیم تا در مراحل بعد سریع‌تر انجام شود
RUN mvn dependency:go-offline

# حالا تمام سورس کد برنامه را کپی می‌کنیم
COPY src ./src

# پروژه را پکیج می‌کنیم و فایل jar را می‌سازیم. از اجرای تست‌ها صرف‌نظر می‌کنیم تا بیلد سریع‌تر شود.
RUN mvn clean package -DskipTests


# --- مرحله ۲: ساخت ایمیج نهایی و اجرایی (Runner) ---
# از یک ایمیج بسیار سبک که فقط Java Runtime Environment (JRE) را دارد، استفاده می‌کنیم
FROM eclipse-temurin:17-jre-jammy

# پوشه کاری را تنظیم می‌کنیم
WORKDIR /app

# !!! مهم: فایل jar را از مرحله "builder" به ایمیج نهایی خود کپی می‌کنیم
# !!! مطمئن شوید نام فایل jar شما صحیح است
COPY --from=builder /app/target/untitled2-1.0-SNAPSHOT.jar app.jar

# دستوری که با شروع به کار کانتینر، برنامه جاوا را اجرا می‌کند
CMD ["java", "-jar", "app.jar"]