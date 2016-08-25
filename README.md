# DataCurator
An android app that can be used to label some images on a computer. You need to create a config JSON that contains the list of tags that you want to classify the images on the folder. Run a python script to run a simpleHTTPServer and then point your IP in the Android App


Usage:

## Step1: Prepare an input JSON based on the image folders to tag ##

Edit the generateInputJSON.py with your local IP address and the destination folders to tag
```
cd data/images
python generateInputJSON.py 
```
This generates the input.json and save it here


## Step2: Run the server on the root folder with images ##
Make sure input.json is saved in the sample location where you are running the SimpleHTTPServerWithUpload.py
```
cd data
sudo python SimpleHTTPServerWithUpload.py 80
```

## Step3: Run the Android App ##

Get the apk from data/DataCurator_V0.1.apk
Make sure your android phone and the server are connected in the same WIFI network. 

Follow the screenshots and tag your images!!

![Login Page](/login.png "Login Page")


![Curate Page](/curate.png "Tagging Page")


![Upload results](/onupload_button.png "Upload by clicking on the upload button")
