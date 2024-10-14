const CountryService = {
    async fetchCountries() {
        try {
            const response = await fetch('https://restcountries.com/v3.1/all');
            const countryData = await response.json();

            const countryList = countryData
                .map(country => ({
                    code: country.cca2,
                    name: country.name.common,
                }))
                .sort((a, b) => a.name.localeCompare(b.name));

            return countryList;
        } catch (error) {
            console.error('Error fetching countries:', error);
            return [];
        }
    },

    async fetchLanguages() {
        try {
            const response = await fetch('https://restcountries.com/v3.1/all');
            const countryData = await response.json();

            const languageSet = new Set();
            countryData.forEach(country => {
                if (country.languages) {
                    Object.values(country.languages).forEach(language => {
                        languageSet.add(language);
                    });
                }
            });

            const languageList = Array.from(languageSet).sort((a, b) => a.localeCompare(b));

            return languageList;
        } catch (error) {
            console.error('Error fetching languages:', error);
            return [];
        }
    }
};

export default CountryService;
