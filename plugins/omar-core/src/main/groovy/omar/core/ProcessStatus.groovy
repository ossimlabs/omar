package omar.core

/**
 * Created by gpotts on 4/16/15.
 */
public enum ProcessStatus {
   READY('READY'),
   RUNNING('RUNNING'),
   PAUSED('PAUSED'),
   CANCELED('CANCELED'),
   FINISHED('FINISHED'),
   FAILED('FAILED')

   String name

   ProcessStatus(String name) {
      this.name = name
   }

}