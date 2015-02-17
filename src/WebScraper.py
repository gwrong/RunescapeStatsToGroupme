'''
Created on Feb 4, 2015

@author: Graham
'''

import requests
import bs4
import os
import time

class WebScraper:
    
    def get_stats(self, username):
        webPage = requests.get('http://services.runescape.com/m=hiscore_oldschool/hiscorepersonal.ws?user1=' + username.replace(' ', '%A0'))
        soup = bs4.BeautifulSoup(webPage.text)
        
            
        skillSoup = soup.select('div#contentHiscores td[align=left] a[href^=overall.ws]')
        self.skillNames = [(name.string).replace('\n', '') for name in skillSoup if name.string != ' ']
    
        skillStats = soup.select('div#contentHiscores td[align=right]')
        skillStats = [tag.string for tag in skillStats if tag.img == None]
        
        
        rankList = skillStats[3::3]
        levelList = skillStats[4::3]
        xpList = skillStats[5::3]
    
        combined = zip(rankList, levelList, xpList)
        dictBySkill = dict(zip(self.skillNames, combined))
        return dictBySkill
    
    def filter_stats_by(self, option, stats):
        result = ''
        numSkills = len(stats.keys())
        count = 1
        index = float('inf')
        if (option == 'rank'):
            index = 0
        elif (option == 'level'):
            index = 1
        elif (option == 'xp'):
            index = 2
        else:
            raise ValueError('Not a valid option')
        for skill in self.skillNames:
            trio = stats[skill]
            result = result + skill + ':' + trio[index]
            if (count < numSkills):
                result = result + '|'
                count = count + 1
        return result
        
        
    
if __name__ == '__main__':
    scraper = WebScraper()
    while(1):
        directory = "Enter directory of this file with double backslashes"
        for fileName in os.listdir(directory):
            if fileName.endswith(".in"):
                if (os.path.exists(directory + '\\' + fileName)):
                    curFile = open(directory + '\\' + fileName, 'r')
                    fileString = curFile.read();
                    curFile.close()
                    os.remove(directory + '\\' + fileName)
                    print fileString
                    stats = scraper.get_stats(fileString)
                    levels = scraper.filter_stats_by('level', stats)
                    curFile = open(directory + '\\' + fileName[:-3] + '.out', 'w')
                    curFile.truncate()
                    curFile.write(str(levels))
                    curFile.close();
        time.sleep(15)
        
        
        
        
#     string = scraper.filter_stats_by('level', stats)
#     file = open('levels.txt', 'w')
#     file.write(string)
#     file.close();
#     string = scraper.filter_stats_by('rank', stats)
#     file = open('ranks.txt', 'w')
#     file.write(string)
#     file.close();
#     string = scraper.filter_stats_by('xp', stats)
#     file = open('xp.txt', 'w')
#     file.write(string)
#     file.close();
        
        
        
        
