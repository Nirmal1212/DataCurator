This folder contains 

[1] generateInputJSON.py 

[2] SimpleHTTPServer.py



# Generate INPUT JSON #

## Step1: Prepare an input JSON based on the image folders to tag ##

Get the local IP address of the machine by running ```ifconfig``` or ```ipconfig```

Edit the generateInputJSON.py with your local IP address and the destination folders to tag

Run the generateInputJSON.py to generate the config JSON

```
cd data/images
python generateInputJSON.py 
```
This generates the input.json and save it here



# Simple HTTP Server #

## Step2: Run the server on the root folder with images ##

Make sure input.json is saved in the sample location where you are running the SimpleHTTPServerWithUpload.py

Run SimpleHTTPServer.py to start a simple server that will serve the images and the config file to the Android.
```
cd data
sudo python SimpleHTTPServerWithUpload.py 80
```
