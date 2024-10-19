import ApiClient from '../api/ApiClient';

const UserService = {
    getProfile: async () => {
        try {
            return await ApiClient.get('/users/me');
        } catch (error) {
            console.error('Error fetching user profile:', error);
            throw error;
        }
    },

    saveProfile: async (updatedProfile) => {
        try {
            return await ApiClient.put(`/users/me`, updatedProfile);
        } catch (error) {
            console.error('Error saving user profile:', error);
            throw error;
        }
    },

    uploadProfileImage: async (formData, userId) => {
        try {
            return await ApiClient.put(`/flash/images/profile/${userId}`, formData);
        } catch (error) {
            console.error('Error uploading profile image:', error);
            throw error;
        }
    },
    checkUsernameAvailability: async (username, currentUsername) => {
        if (username === currentUsername) {
            return true;
        }
        try {
            const response = await ApiClient.get(`/users/check-username/${username}`);
            console.log('API response:', response);

            if (response && response.isAvailable !== undefined) {
                return response.isAvailable;
            } else {
                console.error('Unexpected response structure:', response);
                return false;
            }
        } catch (error) {
            console.error('Error checking username availability:', error);
            throw error;
        }
    },
};

export default UserService;