# ═══════════════════════════════════════════════════════════════════
# Russify Android Build Container
# Optimized multi-stage build for Android app compilation
# ═══════════════════════════════════════════════════════════════════

# Base image with Java 17 for Android SDK tools; Java 11 is installed alongside it for Gradle toolchains
FROM eclipse-temurin:17-jdk-jammy AS base

# Metadata
LABEL maintainer="Russify Team"
LABEL description="Android build environment for Russify app"
LABEL version="1.0"

# Environment variables for Android SDK
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA11_HOME=/opt/java11
ENV ORG_GRADLE_JAVA_INSTALLATIONS_PATHS=/opt/java11
ENV PATH=$PATH:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/emulator

# Install system dependencies
RUN apt-get update && apt-get install -y \
    # Build essentials
    unzip \
    wget \
    curl \
    git \
    openjdk-11-jdk \
    # Required libraries for Android SDK
    libc6 \
    libstdc++6 \
    libpulse0 \
    libglu1-mesa \
    # Required for emulator (optional, but good to have)
    libqt5widgets5 \
    libqt5gui5 \
    libqt5core5a \
    libxcb-cursor0 \
    libx11-6 \
    libnss3 \
    libxcomposite1 \
    libxcursor1 \
    libxi6 \
    libxrandr2 \
    libxdamage1 \
    libxtst6 \
    libdbus-1-3 \
    libasound2 \
    # Cleanup
    && rm -rf /var/lib/apt/lists/*

# Normalize the Java 11 location so Gradle toolchains work on both amd64 and arm64 images
RUN ln -s "$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")" /opt/java17 && \
    ln -s /usr/lib/jvm/java-11-openjdk-* /opt/java11

# Download and install Android command line tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget -q -O /tmp/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest && \
    rm /tmp/cmdline-tools.zip

# Accept licenses and install SDK components
RUN yes | sdkmanager --licenses && \
    sdkmanager --update && \
    sdkmanager \
    "platform-tools" \
    "platforms;android-35" \
    "platforms;android-34" \
    "build-tools;35.0.0" \
    "build-tools;34.0.0" \
    "cmdline-tools;latest" \
    "extras;android;m2repository" \
    "extras;google;m2repository"

# ═══════════════════════════════════════════════════════════════════
# Builder stage
# ═══════════════════════════════════════════════════════════════════
FROM base AS builder

WORKDIR /workspace

# Copy Gradle wrapper first (for caching)
COPY gradlew gradlew.bat ./
COPY gradle gradle/

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy dependency files (for caching)
COPY build.gradle.kts settings.gradle.kts version.gradle.kts ./
COPY gradle.properties ./
COPY app/build.gradle.kts app/

# Download dependencies (this layer will be cached)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY . .

# Build arguments for build type and flavor
ARG BUILD_TYPE=debug
ARG FLAVOR=dev

# Print version info
RUN ./gradlew --version

# Default build command (can be overridden)
CMD ./gradlew assemble${FLAVOR^}${BUILD_TYPE^} --no-daemon --stacktrace

# ═══════════════════════════════════════════════════════════════════
# Development stage (for development with volume mounts)
# ═══════════════════════════════════════════════════════════════════
FROM base AS development

WORKDIR /workspace

# Copy gradle wrapper
COPY gradlew gradlew.bat ./
COPY gradle gradle/
RUN chmod +x ./gradlew

# This stage expects source code to be mounted as volume
# Used for: docker run -v $(pwd):/workspace ...

CMD ["/bin/bash"]

# ═══════════════════════════════════════════════════════════════════
# Testing stage
# ═══════════════════════════════════════════════════════════════════
FROM builder AS testing

ARG FLAVOR=dev

# Run unit tests
RUN ./gradlew test${FLAVOR^}DebugUnitTest --no-daemon --stacktrace

# Run lint checks
RUN ./gradlew lint${FLAVOR^}Debug --no-daemon --stacktrace

CMD ["./gradlew", "check", "--no-daemon"]
