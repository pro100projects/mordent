export const ERROR_INITIAL_STATE = {
  code: null,
  description: null
};

export const SLICE_INITIAL_STATE = (sliceDataInitialState = null) => ({
  data: sliceDataInitialState,
  isLoading: false,
  error: ERROR_INITIAL_STATE
});
