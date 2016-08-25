import json, os, fnmatch

def find_files(folder,pattern1,pattern2=None,pattern3 = None):
    for root, dirs, files in os.walk(folder):
        for name in files:
#             print root,name
            if fnmatch.fnmatch(name,pattern1):
                yield os.path.join(root,name),root
            elif pattern2 and fnmatch.fnmatch(name,pattern2):
                yield os.path.join(root,name),root
            elif pattern3 and fnmatch.fnmatch(name,pattern3):
                yield os.path.join(root,name),root

baseURL = 'http://192.168.0.54/'
folder = 'images/men'
images = []
data = {}
for fileName,folder in find_files(folder,'*.jpeg','*.jpg','*.png'):
    img = os.path.join(baseURL,fileName)
    if folder not in data:
        data[folder] = []
    data[folder].append(img)

# print data
with open('input.json','w') as f:
    json.dump(data,f,indent=2)

