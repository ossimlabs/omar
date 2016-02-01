package omar.core

class ProxyController
{

  def index()
  {
   // println params

//    def urlText = params.url.decodeBase64()
    def urlText = params.url
    def url = new String( urlText ).toURL()

//    println "${'*' * 40}"
//    println url
//    println "${'*' * 40}"

    //render file: url.bytes
    url.withInputStream {
      response.outputStream << it
    }
  }
}
