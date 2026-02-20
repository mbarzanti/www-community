package opensearchservice

import (
	"bpap-mf-logspooler/model"
	"context"
	"crypto/tls"
	"encoding/json"
	"errors"
	"net"
	"net/http"
	"strings"
	"time"

	"github.com/opensearch-project/opensearch-go"
	"github.com/opensearch-project/opensearch-go/opensearchapi"
	"github.com/rs/zerolog/log"
)

type OpenSearchService struct {
	Client *opensearch.Client
	Config *Config
}

func NewOpenSearchService(cfg *Config) *OpenSearchService {
	opensearchservice := new(OpenSearchService)
	client, err := opensearch.NewClient(opensearch.Config{
		Transport: &http.Transport{
			TLSClientConfig:       &tls.Config{InsecureSkipVerify: true},
			ResponseHeaderTimeout: time.Duration(cfg.Timeout) * time.Millisecond,
			DialContext: (&net.Dialer{
				Timeout: time.Duration(cfg.Timeout) * time.Millisecond,
			}).DialContext,
		},
		Addresses: []string{cfg.Hostname},
		Username:  cfg.Username, // For testing only. Don't store credentials in code.
		Password:  cfg.Password,
	})
	if err != nil {
		log.Fatal().Msg(err.Error())
	}
	opensearchservice.Config = cfg
	opensearchservice.Client = client

	return opensearchservice
}

func (service *OpenSearchService) InsertMessage(mflog *model.MFStep) error {
	b, err := json.Marshal(mflog)
	if err != nil {
		log.Err(err).Msg("failed serialize document ")

		return err
	}
	log.Trace().Msg(string(b))
	mapping := strings.NewReader(string(b))
	req := opensearchapi.IndexRequest{
		Index: service.Config.Index,
		Body:  mapping,
	}
	insertResponse, err := req.Do(context.Background(), service.Client)
	if err != nil {
		log.Err(err).Msg("failed to insert document ")
		return err
	} else {
		log.Debug().Msg(string(insertResponse.StatusCode) + " - " + insertResponse.String())

		if insertResponse.StatusCode != 201 {
			return errors.New("failed to insert document ")
		}

	}
	return nil
}
