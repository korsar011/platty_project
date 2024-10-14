// DeckService.js
import ApiClient from '../api/ApiClient';

const DECK_BASE_URL = '/flash/decks';

const DeckService = {
    async getAllDecks() {
        try {
            return await ApiClient.get(`${DECK_BASE_URL}`);
        } catch (error) {
            console.error('Error fetching decks:', error);
            return [];
        }
    },

    async getDeckImage(deckId) {
        try {
            return await ApiClient.get(`${DECK_BASE_URL}/${deckId}/image`);
        } catch (error) {
            console.error(`Error fetching image for deck ${deckId}:`, error);
            return null;
        }
    },

    async createDeck(createDeckRequest) {
        try {
            return await ApiClient.post(`${DECK_BASE_URL}`, createDeckRequest);
        } catch (error) {
            console.error('Error creating deck:', error);
            return null;
        }
    },

    async updateDeck(deckId, updateDeckRequest) {
        try {
            return await ApiClient.put(`${DECK_BASE_URL}/${deckId}`, updateDeckRequest);
        } catch (error) {
            console.error(`Error updating deck ${deckId}:`, error);
            return null;
        }
    },

    async getDeckInfo(deckId) {
        try {
            return await ApiClient.get(`${DECK_BASE_URL}/info?deckId=${deckId}`)
        } catch (error){
            console.error(`Error fetching deck info of deck with id ${deckId}:`, error);
            return null;
        }
    },

    async deleteDeck(deckId) {
        try {
            await ApiClient.delete(`${DECK_BASE_URL}/${deckId}`);
            return true;
        } catch (error) {
            console.error(`Error deleting deck ${deckId}:`, error);
            return false;
        }
    },
};

export default DeckService;
