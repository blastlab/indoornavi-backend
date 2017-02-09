#!/bin/bash

# Blocks until given container can be reached by ping.
# Useful when other container must finish before this container can start work.

write_error_and_exit() {
	echo $1 >&2
	exit 1
}

wait_for_all_containers_to_end() {
	for i in "${HOSTS[@]}"; do
		wait_for_container_to_end $i
	done
}

wait_for_container_to_end() {
	echo "Waiting for container $1 to end..."
	while (ping -c 1 -W 2 $1 &> /dev/null); do
		sleep 1
	done
	echo "Container $1 ended"
}

HOSTS=()

while [[ $# -gt 0 ]]
do
	case "$1" in
		-c)
			if [[ $2 == "" ]]; then break; fi
			HOSTS+=($2)
			shift 2
			;;
		--)
			shift
			CLI="$@"
			break
			;;
		*)
			write_error_and_exit "Unknown argument: $1"
			;;
	esac
done

if [[ ${#HOSTS[@]} -eq 0 ]]; then
	write_error_and_exit "No containers given"
fi

wait_for_all_containers_to_end

if [[ $CLI != "" ]]; then
	exec $CLI
fi
