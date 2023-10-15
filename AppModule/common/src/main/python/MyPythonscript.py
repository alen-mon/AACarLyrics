def main(song):
    from azapi import AZlyrics

    api = AZlyrics("google")

    # Set the title using the argument provided
    api.title = song

    lyrics = api.getLyrics()
    title = api.title
    artist = api.artist

    return (title, artist, lyrics)
