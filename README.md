# Product Price Arbitrage & Profitability Platform

## Overview

This project is a platform for **analyzing price differences between
Digikala and other online retailers** to help sellers identify the most
profitable products to sell, the right price points, and the best
sources for procurement.

The platform enables sellers --- small, medium, and large --- to make
data-driven decisions by automating:\
- Product crawling from multiple sites\
- Product matching across sites (text & image similarity)\
- Profitability calculations (including operational costs and
commissions)\
- Notifications for profitable opportunities

------------------------------------------------------------------------

## Problem & Opportunity

-   **Price differences** exist between Digikala and other online
    marketplaces.\
-   Sellers need tools to answer critical questions:
    -   What should I sell?\
    -   At what price?\
    -   From which supplier?\
-   Manual analysis is time-consuming and inefficient.

------------------------------------------------------------------------

## Solution

The platform provides:\
- **Website Crawling:** Extracts product data from target websites using
CSS selectors.\
- **Automated Product Matching:**\
- **Textual similarity** using LLMs (titles & specifications).\
- **Visual similarity** using neural networks (ResNet50).\
- **Profitability Calculation:**\
- Includes Digikala's commission and operational costs.\
- Calculates ROI for each opportunity.\
- **Search & Notifications:** Allows saving searches and getting alerts
for new opportunities.

------------------------------------------------------------------------

## System Architecture

1.  **Crawl a Site** -- Collect product data using Scrapy.\
2.  **Search Digikala** -- Find potential matches via API.\
3.  **LLM Matching** -- Compare text attributes using LLaMA3 (via
    Ollama).\
4.  **Image Matching** -- Use ResNet50 for visual similarity check.\
5.  **Profit Calculation** -- Compute net profit after costs.\
6.  **Save to Database** -- Store matches and calculations in
    Elasticsearch.

------------------------------------------------------------------------

## Key Features

-   Add any website to crawl by simply providing its CSS selectors.\
-   Search and match products automatically.\
-   Store search results and get notified about future profitable
    matches.\
-   Support for both **small/medium sellers** (what to sell) and **large
    sellers** (what price to sell).\
-   Supports **reverse arbitrage** use cases.

------------------------------------------------------------------------

## Technology Stack

-   **Crawler:** Scrapy (Python)\
-   **AI / LLM:** Ollama + LLaMA3 (local inference)\
-   **Image Matching:** ResNet50 (Python)\
-   **Backend API:** Spring Boot (Java)\
-   **Database & Search:** Elasticsearch\
-   **Frontend:** React.js with refine.dev

------------------------------------------------------------------------

------------------------------------------------------------------------

## Risks

-   Returned or defective products from Digikala.\
-   Incorrect product matches leading to wrong purchases.\
-   Shipping cost miscalculations.

------------------------------------------------------------------------