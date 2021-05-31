#url-shortener

Java 11
```shell
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk11
```

## Build
```shell
brew install gradle
gradle build
```

## Run

Run this application using `Docker`:
```shell
docker-compose up
```

## Application Endpoints

### Create Shortened URL
**Description**:  Creates a Shortened URL by providing a URL and an optional ID base64. If no ID is provided then one will be randomly generated.

**Sample Request**
```shell
POST /v1/shortened/url HTTP/1.1
Host: 0.0.0.0:8080
Content-Type: application/json
Content-Length: 89

{
    "url" : "https://flyancerecords.bandcamp.com/album/kas-st-road-to-nowhere-road1"
}

```
**Sample Response**
```shell
{
    "message": "ID Created, please use shortened URL: http://localhost:8080/v1/shortened/QMlwrc",
    "id": "QMlwrc",
    "url": "https://flyancerecords.bandcamp.com/album/kas-st-road-to-nowhere-road1"
}
```

### Redirect using Shortened URL
**Description**: Use Shortened URL to be redirected

**Sample Request**
```shell
GET /v1/shortened/QMlwrc HTTP/1.1
Host: localhost:8080
```

**Sample Response**
```shell
<!DOCTYPE html>
<html xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml">

<head>
	<title>KAS:ST - Road To Nowhere [ROAD1] | Kas:st | Flyance Records</title>



....
```

## Postman

If you use Postman for submitting requests, there is a collection
`url-shortener.postman_collection.json` in the project root with sample requests to the above endpoints