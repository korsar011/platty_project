import ApiClient from '../api/ApiClient';

const CardService = {
    async getCardById(cardId) {
        return ApiClient.get(`/flash/cards/${cardId}`);
    },

    async getNextCardFromDeck(deckId) {
        return ApiClient.get(`/flash/cards/next?deckId=${deckId}`);
    },

    async getNextCardFromAllUserDecks() {
        return ApiClient.get('/flash/cards/master-next');
    },

    async updateCardRating(cardId, userChoice) {
        return ApiClient.put(`/flash/cards/${cardId}/rating`, { userChoice });
    },

    async getAllCardsByDeckId(deckId) {
        return ApiClient.get(`/flash/cards?deckId=${deckId}`);
    },

    async createCard(cardData) {
        return ApiClient.post('/flash/cards', cardData);
    },

    async updateCard(cardId, cardData) {
        return ApiClient.put(`/flash/cards/${cardId}`, cardData);
    },

    async deleteCard(cardId) {
        return ApiClient.delete(`/flash/cards/${cardId}`);
    },

    async createDefaultCard(deckId) {
        return ApiClient.post(`/flash/cards/default?deckId=${deckId}`);
    },

    async updateCardBackTextFromFrontTextTranslation(translateRequest) {
        return ApiClient.put('/flash/cards/translate', translateRequest);
    },
};

export default CardService;
