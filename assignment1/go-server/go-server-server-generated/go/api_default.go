/*
 * Album Store API
 *
 * CS6650 Fall 2023
 *
 * API version: 1.0.0
 * Contact: i.gorton@northeasern.edu
 * Generated by: Swagger Codegen (https://github.com/swagger-api/swagger-codegen.git)
 */
package swagger

import (
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"io"
	"net/http"
	"strconv"
)

func GetAlbumByKey(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	vars := mux.Vars(r)

	_, err := strconv.Atoi(vars["albumID"])
	if err != nil {
		handleError(w, http.StatusBadRequest)
		return
	}
	albumInfo := AlbumInfo{
		Artist: "Sex Pistols",
		Title:  "Never Mind The Bollocks!",
		Year:   "1977",
	}
	jsonResponse, err := json.Marshal(albumInfo)
	if err != nil {
		handleError(w, http.StatusNotFound)
		return
	}
	w.WriteHeader(http.StatusOK)
	w.Write(jsonResponse)
}

func NewAlbum(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	reader, err := r.MultipartReader()
	if err != nil {
		fmt.Printf("Error creating MultipartReader: %s", err)
		handleError(w, http.StatusInternalServerError)
		return
	}
	for {
		part, err := reader.NextPart()
		if err == io.EOF {
			break
		}
		if err != nil {
			fmt.Printf("Error reading part: %s", err)
			handleError(w, http.StatusInternalServerError)
			return
		}
		// storing files to be added
		part.Close()

	}
	responseData := ImageMetaData{
		AlbumID:   "string",
		ImageSize: "string",
	}
	jsonResponse, err := json.Marshal(responseData)
	w.WriteHeader(http.StatusOK)
	w.Write(jsonResponse)
}
func handleError(w http.ResponseWriter, statusCode int) {
	errorMsg := ErrorMsg{Msg: "string"}
	jsonErrorResponse, _ := json.Marshal(errorMsg)
	w.WriteHeader(statusCode)
	w.Write(jsonErrorResponse)
}
