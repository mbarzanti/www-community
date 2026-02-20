package mq

import (
	"bpap-mf-logspooler/metrics"
	"bpap-mf-logspooler/model"
	"bpap-mf-logspooler/opensearchservice"
	"fmt"
	"time"

	"github.com/ibm-messaging/mq-golang-jms20/jms20subset"
	"github.com/ibm-messaging/mq-golang-jms20/mqjms"
	"github.com/opentracing/opentracing-go"
	"github.com/opentracing/opentracing-go/ext"
	"github.com/rs/zerolog/log"
	"golang.org/x/text/encoding/charmap"
)

type MQConsumer struct {
	TransactedContext         jms20subset.JMSContext
	Consumer                  jms20subset.JMSConsumer
	Config                    *Config
	CurrentTransactionMessage int
}

func NewMQConsumer(cfg *Config) *MQConsumer {
	jmsConsumer := new(MQConsumer)
	jmsConsumer.Config = cfg
	jmsConsumer.CurrentTransactionMessage = 0
	cf := mqjms.ConnectionFactoryImpl{
		QMName:      cfg.QueueManager,
		Hostname:    cfg.Hostname,
		PortNumber:  cfg.Port,
		ChannelName: cfg.Channel,
		UserName:    cfg.UserName,
		Password:    cfg.Password,

		TLSCipherSpec:    cfg.TLSCipherSpec,
		TLSClientAuth:    cfg.TLSClientAuth,
		KeyRepository:    cfg.KeyRepository,
		CertificateLabel: cfg.CertificateLabel,
	}
	jmscontext, errCtx := cf.CreateContext()
	if jmscontext != nil {
		defer jmscontext.Close()
	}

	if errCtx != nil {
		log.Err(errCtx.GetLinkedError()).Msg("Errore connessione MQ")
		log.Fatal().Msg(errCtx.GetErrorCode() + " -  " + errCtx.GetReason() + " - ")
	}

	queue := jmscontext.CreateQueue(cfg.QueueName)

	transactedContext, errCtx := cf.CreateContextWithSessionMode(jms20subset.JMSContextSESSIONTRANSACTED)

	jmsConsumer.TransactedContext = transactedContext

	consumer, conErr := transactedContext.CreateConsumer(queue)
	if conErr != nil {
		log.Fatal().Msg("Jms Error : " + conErr.GetErrorCode() + " : " + conErr.GetReason())
	}

	jmsConsumer.Consumer = consumer

	return jmsConsumer

}

func (mqconsumer *MQConsumer) Consume(client *opensearchservice.OpenSearchService) {

	rcvBody, errJMS := mqconsumer.Consumer.ReceiveStringBodyNoWait()

	if errJMS != nil {
		log.Fatal().Msg("Jms Error : " + errJMS.GetErrorCode() + " : " + errJMS.GetReason())
	}

	if rcvBody != nil {
		mqconsumer.CurrentTransactionMessage++
		sp := opentracing.StartSpan("consume log message")
		defer sp.Finish()
		metrics.IncTotalMetric()
		var input = *rcvBody
		lenbody := len(*rcvBody)
		if lenbody != 801 {
			ext.Error.Set(sp, true)
			log.Warn().Msgf("Lunghezza messaggio non corretta %d\n", lenbody)
			mqconsumer.TransactedContext.Commit()
			metrics.IncMessageErrorMetric()
			return
		}
		log.Trace().Msg(*rcvBody)
		if mqconsumer.Config.DecodeBody {
			input = decodeString(*rcvBody)
		}

		log.Trace().Msg(input)
		mflog := model.NewMFStep(input, mqconsumer.Config.TimeStampFormat)

		metrics.IncStepMetrics(mflog.Step.Step)
		//sp.LogFields("Send to Opensearch")
		err := client.InsertMessage(mflog)
		//sp.LogFields("Send to Opensearch")
		if err != nil {
			ext.Error.Set(sp, true)
			mqconsumer.TransactedContext.Rollback()
			mqconsumer.CurrentTransactionMessage = 0
			mqconsumer.Close()
			log.Fatal().Err(err).Msg("Impossibile inserire documento")
		}
		if mqconsumer.CurrentTransactionMessage > mqconsumer.Config.MaxTransactionMessage {
			log.Info().Msg("Eseguo commit transazione max message Received")
			mqconsumer.TransactedContext.Commit()
			mqconsumer.CurrentTransactionMessage = 0
		}

	} else {

		mqconsumer.TransactedContext.Commit()
		mqconsumer.CurrentTransactionMessage = 0
		metrics.IncSleepMq()
		time.Sleep(time.Duration(mqconsumer.Config.SleepNoMessage) * time.Millisecond)
	}

}

func (mqconsumer *MQConsumer) Close() {
	mqconsumer.Consumer.Close()
	mqconsumer.TransactedContext.Close()
}

func decodeString(input string) string {
	data := []byte(input)
	decoder := charmap.CodePage037.NewDecoder()
	output, error := decoder.Bytes(data)
	if error != nil {
		fmt.Println("Error ", error)
	}
	r := string(output[:])
	return r
}
