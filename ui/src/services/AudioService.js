import ApiClient from '../api/ApiClient';

const AUDIO_BASE_URL = '/flash/audio'; // Адрес вашего API

const AudioService = {
    async uploadAudio(cardId, audioBlob) {
        const formData = new FormData();
        formData.append('file', audioBlob, `audio_${cardId}.ogg`);
        formData.append('cardId', cardId);

        for (let pair of formData.entries()) {
            console.log(pair[0] + ', ' + pair[1]);
        }

        const response = await ApiClient.post(`${AUDIO_BASE_URL}/upload?cardId=${cardId}`, formData);

        return response.data;
    },

    async generateAudio(audioRequest) {
        try {
            const response = await ApiClient.post(`${AUDIO_BASE_URL}/generate`, audioRequest);
            return response.data;
        } catch (error) {
            console.error('Error generating audio:', error);
            throw error;
        }
    },
};

export default AudioService;