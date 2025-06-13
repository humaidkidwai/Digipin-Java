# DIGIPIN Java Package (Maven)

<div align="center" style="display: flex; justify-content: center; align-items: center; gap: 20px;">
  <img src="https://dev.cept.gov.in/mydigipin/_next/image?url=%2Fmydigipin%2Fimages%2Findiapost_logo_v2.webp&w=1920&q=75" alt="India Post" width="120"/>
</div>

## A Geospatial Addressing Solution by India Post

DIGIPIN (Digital PIN) is a 10-character alphanumeric geocode developed by the Department of Posts, India. It provides a precise, user-friendly way to encode geographic coordinates that can be easily shared and decoded back to latitude/longitude pairs.

This open-source **Java** project presents a public **.jar** package to generate and decode DIGIPINs, supporting geolocation services, postal logistics, and spatial analysis applications.

## 🏛️ About DIGIPIN

The Department of Posts, India, has published a [technical document](https://www.indiapost.gov.in/VAS/DOP_PDFFiles/DIGIPIN%20Technical%20document.pdf) detailing DIGIPIN and how it works.


## 📦 How to Use

To use this library in your Maven project:

1. **Add the GitHub Packages repository**
2. **Add the dependency to your `pom.xml`**

### 🛠 Add to `pom.xml`

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/humaidkidwai/Digipin-Java</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>io.github.humaidkidwai</groupId>
    <artifactId>digipin</artifactId>
    <version>0.1.0</version>
  </dependency>
</dependencies>
```

## 🔐 Authentication Required
To download this package, Maven must authenticate with GitHub Packages.

Add the following to your ~/.m2/settings.xml:
```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_PAT</password>
  </server>
</servers>
```
💡 Your GitHub Personal Access Token (PAT) must include the read:packages scope.
If using a private repository, include the repo scope as well.
---