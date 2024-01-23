package com.example.umatchapp.api;

import java.util.List;

public class UMatchApiResponse {
    private List<Prediction> predictions;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public static class Prediction {
        private List<Float> confidences;
        private List<String> displayNames;
        private List<String> ids;

        public List<Float> getConfidences() {
            return confidences;
        }

        public void setConfidences(List<Float> confidences) {
            this.confidences = confidences;
        }

        public List<String> getDisplayNames() {
            return displayNames;
        }

        public void setDisplayNames(List<String> displayNames) {
            this.displayNames = displayNames;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }
}


