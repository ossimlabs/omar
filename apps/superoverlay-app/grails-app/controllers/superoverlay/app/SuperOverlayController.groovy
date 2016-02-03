package superoverlay.app

class SuperOverlayController
{
  def superOverlayService

  def index()
  {
    render contentType: 'application/vnd.google-earth.kml+xml', text: superOverlayService.serviceMethod()
  }
}
