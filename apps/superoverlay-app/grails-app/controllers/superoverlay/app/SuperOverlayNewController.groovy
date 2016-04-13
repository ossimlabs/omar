package superoverlay.app

class SuperOverlayNewController
{
  def superOverlayService

  def index()
  {
    render contentType: 'application/vnd.google-earth.kml+xml', text: superOverlayService.serviceMethod()
  }
}
