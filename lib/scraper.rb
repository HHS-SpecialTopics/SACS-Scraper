require 'nokogiri'
require 'open-uri'
require 'json'
require 'safe_yaml'

PAGE_URL = 'http://www.sacs.k12.in.us/site/default.aspx?PageType=14&DomainID=4&PageID=1&ModuleInstanceID=74&ViewID=7070de72-c6ac-43a7-b8eb-103562708ba5&IsMoreExpandedView=True'.freeze

page = Nokogiri::HTML(open(PAGE_URL))
links = page.css('h1.ui-article-title a')

index = 0
Dir.mkdir('json') unless Dir.exist?('json')

links.each do |link|
  subpage = Nokogiri::HTML(open(URI.join(PAGE_URL, link['href'])))
  title = subpage.css('div.ui-widget-header h1 span')[0].text.strip
  text = subpage.css('div.ui-widget-detail')[0].to_s
                .gsub("\r\n", ' ')
                .gsub('src="/', 'src="http://www.sacs.k12.in.us/').strip

  json = { title: title, content: text,
           status: 'publish', 'categories[]' => [7], 'tags[]' => [6,8] }
  json_obj = JSON.pretty_generate json

  File.open("json/#{index}.json", 'w') do |file|
    file.write(json_obj)
  end

  index += 1

  print '.'
end

cache = 0
cache = File.read('config/cache.dat') if File.exist?('config/cache.dat')

if (index - cache) > 0
  info_hash = SafeYAML.load(File.read('config/info.yml'), 'config/info.yml')
  result_yaml = { 'User' => info_hash['User'], 'Pass' => info_hash['Pass'] }
  url = info_hash['Url']
  matrix = []

  (0..(index - cache - 1)).each do |i|
    item = { 'Files' => "json/#{i}.json", 'Command' => 'post', 'Url' => url }
    matrix.unshift item
  end

  result_yaml['Matrix'] = matrix

  File.open('jsontodb-config.yml', 'w') do |file|
    file.write(result_yaml.to_yaml)
  end
end

File.open('config/cache.dat', 'w') do |file|
  file.write(cache)
end
