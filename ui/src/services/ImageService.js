import ApiClient from '../api/ApiClient';

const IMAGES_URL = '/flash/images';

const ImageService = {
    async getImageByUrl(url) {
        try {
            const response = await ApiClient.get(`${IMAGES_URL}`, {
                params: {
                    url: url,
                },
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching image by URL:', error);
            throw error;
        }
    },

    async uploadCardImage(file, cardId) {
        try {
            const response = await ApiClient.postMultipartFile(`${IMAGES_URL}/for-card`, file, { cardId });
            return response.url;
        } catch (error) {
            console.error('Error uploading card image:', error);
            throw error;
        }
    },

    async uploadDeckImage(file, deckId) {
        try {
            const response = await ApiClient.postMultipartFile(`${IMAGES_URL}/for-deck`, file, { deckId });
            console.log("Response from uploadDeckImage:", response);
            if (!response || !response.url) {
                throw new Error('Invalid response: no URL found.');
            }
            console.log("Image URL uploaded: " + response.url);
            return response.url;
        } catch (error) {
            console.error('Error uploading deck image:', error);
            throw error;
        }
    },

    async uploadPremiumCardImageFromText(imageRequest) {
        try {
            const response = await ApiClient.post(`${IMAGES_URL}/premium/card`, imageRequest);
            console.log("Response from uploadPremiumCardImageFromText:", response.data);
            return response.data.url;
        } catch (error) {
            console.error('Error uploading premium card image from text:', error);
            throw error;
        }
    },

    async uploadPremiumDeckImageFromText(text, deckId) {
        try {
            const response = await ApiClient.post(`${IMAGES_URL}/premium/deck`, null, {
                params: { text, deckId },
            });
            return response.data.url;
        } catch (error) {
            console.error('Error uploading premium deck image from text:', error);
            throw error;
        }
    },

    async getImageById(imageId) {
        try {
            const response = await ApiClient.get(`${IMAGES_URL}/${imageId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching image by ID:', error);
            throw error;
        }
    },

    async deleteImage(imageId) {
        try {
            await ApiClient.delete(`${IMAGES_URL}/${imageId}`);
        } catch (error) {
            console.error('Error deleting image:', error);
            throw error;
        }
    }
};

export default ImageService;
