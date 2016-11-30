#!/usr/bin/env bas
echo "Updating dependencies..."
bundle install                  # Make sure Ruby gems (specifically jsontodb) are installed
bundle update

echo "Scraping..."
java -jar bin/scraper.jar       # Scrape the SACS webpage

echo "Scraped!  Uploading..."
jsontodb jsontodb-config.yml    # Post the scraped data

echo "Finished!"

