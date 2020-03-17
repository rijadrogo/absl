// This is a personal academic project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class IndexedMap<K> {
  private List<K> storage;
  private Function<Integer, K> mapper;

  public IndexedMap(int size, Function<Integer, K> mapper) {
    storage = new ArrayList<>(size);
    this.mapper = mapper;
  }

  public IndexedMap(Function<Integer, K> mapper) {
    storage = new ArrayList<>();
    this.mapper = mapper;
  }
}
