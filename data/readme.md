This folder contains 

[1] SimpleHTTPServer.py
[2] generateInputJSON.py 


## Simple HTTP Server ##
Run SimpleHTTPServer.py to start a simple server that will serve the images and the config file to the Android.

Usage:
```
python SimpleHTTPServer.py 80
```

## Generate INPUT JSON ##
Get the local IP address of the machine by running ```ifconfig``` or ```ipconfig```

Edit the generateInputJSON.py to point the right IP address and the folders that you need to use for tagging.

Run the generateInputJSON.py to generate the config JSON
