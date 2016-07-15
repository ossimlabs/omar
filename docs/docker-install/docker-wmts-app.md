# Welcome to the WMTS Service for Docker

WMTS implements the OGC WMTS standard. The WMTS web app uses the WMS and the WFS web services and assumes these services are reachable via a http "GET" call from the WMTS service. The WMTS service wraps the WMTS service call and 1) converts to a WFS query to get the features that cover the WMTS query parameters and 2) calls the WMS service to chip and return the pixel values that satisfy the WMTS request.
