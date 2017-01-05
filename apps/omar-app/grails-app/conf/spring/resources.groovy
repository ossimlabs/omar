// Place your Spring DSL code here
import omar.app.OpenLayersConfig

beans = {
  openLayersConfig( OpenLayersConfig )
  openLayersLayerConverter( OpenLayersConfig.OpenLayersLayerConverter )
}
