# PDF Processing Application

This repository contains a Java application for processing PDF documents, including features such as Word-to-PDF conversion, text extraction, and image generation.

## 1. WordToPdfConverter Class

### Purpose:
- Converts Microsoft Word documents to PDF format.

### Key Features:
- Utilizes the `documents4j` library for Word to PDF conversion.
- Checks if Microsoft Word is running before conversion (optional).

---

## 2. SortingUtil Class

### Purpose:
- Implements a comparator for sorting `WordInfo` objects based on their position.

### Key Features:
- Implements the `Comparator` interface for custom sorting.

---

## 3. Base Class

### Purpose:
- Contains utility methods used by other classes.

### Key Features:
- Provides methods for extracting color, checking font information, and updating font information.

---

## 4. InfoDocUtil Class

### Purpose:
- Converts a list of `WordInfo` objects into a PDF document.

### Key Features:
- Utilizes `PDDocument` and `PDPage` from `pdfbox` library.
- Merges overlapping text positions.
- Sorts `WordInfo` objects based on their position and processes them.

---

## 5. PDFWordExtractor Class

### Purpose:
- Extracts text and formatting information from PDF documents.

### Key Features:
- Extends `PDFTextStripper` from `pdfbox`.
- Extracts information for each word, including font, size, style, and color.
- Supports extraction for specific pages.

---

## 6. WordInfo Class

### Purpose:
- Represents information about a word in a PDF document.

### Key Features:
- Stores information such as word, type list, page number, font, size, style, and color.
- Provides methods to retrieve font-related information.

---

## 7. PDFToImageConverter Class

### Purpose:
- Converts PDF documents to a sequence of images.
- Combines images from multiple PDFs side by side.

### Key Features:
- Utilizes `PDFRenderer` from `pdfbox` for rendering images.
- Combines images horizontally for multiple PDFs.

---

## 8. Config Class

### Purpose:
- Holds configuration constants for the application.

### Key Features:
- Configures batch size for word comparison and image quality.
- Defines color constants for various operations.

---

## 9. Enums Package (Info Class)

### Purpose:
- Contains enums for image quality and document operations.

### Key Features:
- Defines enums for image quality and document operations.

---

## 10. Utils Package

### Purpose:
- Contains utility classes used across the application.

---

## Usage:

- Clone the repository.
- Build and run the application using your preferred Java development environment.
- Customize configuration settings in the `Config` class if needed.

---

This README file provides an overview of the main classes in the application, their purposes, and key features. Follow the usage instructions to integrate and run the application in your environment.

---