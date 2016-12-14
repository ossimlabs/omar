package omar.ingest.metrics

class IngestMetrics {

    String ingestId
    String description

    Date startDate
    Date endDate

    Date startCopy
    Date endCopy

    Date startStaging
    Date endStaging

    /*
    *
    * Values can be
    *
    * READY,
    * RUNNING,
    * PAUSED,
    * CANCELED,
    * FINISHED,
    * FAILED
     */
    String status

    static mapping = {
        ingestId      index: 'ingest_metrics_object_id_idx'
        description   type:  'text'
        startDate     index: 'ingest_metrics_start_date_idx'
        endDate       index: 'ingest_metrics_end_date_idx'
        startCopy     index: 'ingest_metrics_start_copy_idx'
        endCopy       index: 'ingest_metrics_end_copy_idx'
        startStaging  index: 'ingest_metrics_start_staging_idx'
        endStaging    index: 'ingest_metrics_end_staging_idx'
        status        index: 'ingest_metrics_status_idx'
    }

    static constraints = {
        ingestId      nullable:true
        description   nullable:true

        startDate     nullable:true
        endDate       nullable:true

        startDownload nullable:true
        endDownload   nullable:true

        startStaging  nullable:true
        endStaging    nullable:true

        status        nullable:true
    }

}
