export const dateToString = (timestamp) => {
  const MS_PER_MINUTE = 60000;
  const MS_PER_HOUR = 3600000;
  const MS_PER_DAY = 86400000;
  const MS_PER_WEEK = 604800000;
  const MS_PER_MONTH = 2629800000;

  const date = new Date(timestamp * 1000);
  const now = new Date();
  const elapsed = now - date;

  if (elapsed < MS_PER_HOUR) {
    const minutes = Math.floor(elapsed / MS_PER_MINUTE);
    return `${minutes} minutes ago`;
  } else if (elapsed < MS_PER_DAY) {
    const hours = Math.floor(elapsed / MS_PER_HOUR);
    return `${hours} hours ago`;
  } else if (elapsed < MS_PER_WEEK) {
    const days = Math.floor(elapsed / MS_PER_DAY);
    return `${days} days ago`;
  } else if (elapsed < MS_PER_MONTH) {
    const weeks = Math.floor(elapsed / MS_PER_WEEK);
    return `${weeks} weeks ago`;
  } else {
    const months = [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ];
    const month = months[date.getMonth()];
    const day = date.getDate();
    const year = date.getFullYear();
    return `${month} ${day}, ${year}`;
  }
};

export const getDurationFromSeconds = (seconds, back = false) => {
  if (!seconds && !back) return '00:00';
  if (!seconds) return '';
  if (back) seconds = seconds / 1000;
  const minutes = Math.floor(seconds / 60);
  const secondsLeft = Math.round(seconds - minutes * 60);
  return `${minutes}:${secondsLeft < 10 ? `0${secondsLeft}` : secondsLeft}`;
};

export const getDurationFromSongs = (songs) => {
  const seconds = songs.reduce((acc, song) => {
    if (song.metadata && song.metadata.duration !== undefined) {
      return acc + song.metadata.duration / 1000;
    }
    return acc;
  }, 0);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const minutesLeft = Math.round(minutes - hours * 60);
  const secondsLeft = Math.round(seconds - minutes * 60);
  if (hours !== 0) {
    return `${hours} hr ${
      minutesLeft === 0 ? `0` : minutesLeft < 10 ? `0${minutesLeft}` : minutesLeft
    } min`;
  } else {
    return `${minutes} min ${
      secondsLeft === 0 ? `0` : secondsLeft < 10 ? `0${secondsLeft}` : secondsLeft
    } sec`;
  }
};
